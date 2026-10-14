package com.paymentsystem.ticketservice.command.saga;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsystem.ticketservice.command.command.DeleteCheckOutCommand;
import com.paymentsystem.ticketservice.command.command.RollbackMovieSeatsCommand;
import com.paymentsystem.ticketservice.command.command.SavePaymentUrlCommand;
import com.paymentsystem.ticketservice.command.command.UpdateMovieSeatsCommand;
import com.paymentsystem.ticketservice.command.event.*;
import com.paymentsystem.ticketservice.config.VNPayConfig;
import com.paymentsystem.ticketservice.model.MovieResponseModel;
import com.paymentsystem.ticketservice.query.queries.GetMovieDetailQuery;
import com.paymentsystem.ticketservice.util.VNPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Saga
@Slf4j
public class TicketBookingSaga {

    @Autowired
    private transient ObjectMapper objectMapper;

    @Autowired
    private transient VNPayConfig vnPayConfig;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(CheckOutCreatedEvent event) {
        try {
            GetMovieDetailQuery getMovieDetailQuery = GetMovieDetailQuery.builder()
                    .id(event.getMovieId())
                    .build();
            MovieResponseModel movie = queryGateway.query(
                    getMovieDetailQuery,
                    ResponseTypes.instanceOf(MovieResponseModel.class)
            ).join();

            Map<String, String> seatsMap;
            try {
                seatsMap = objectMapper.readValue(movie.getSeatsStatus(), new TypeReference<>() {});
            } catch (Exception e) {
                throw new RuntimeException("Mapping movie seat status fail!");
            }
            String seatCodes = event.getSeats();
            for (String seatCode : seatCodes.split(",")) {
                seatCode = seatCode.trim();
                String currentStatus = seatsMap.get(seatCode);
                if (currentStatus == null) {
                    throw new RuntimeException("Seat " + seatCode + " not found!");
                }
                if (!"AVAILABLE".equals(currentStatus)) {
                    throw new RuntimeException("Seat " + seatCode + " is not available!");
                }
                seatsMap.put(seatCode, "LOCKED");
            }

            SagaLifecycle.associateWith("movieId", event.getMovieId());
            UpdateMovieSeatsCommand updateMovieSeatsCommand = UpdateMovieSeatsCommand.builder()
                    .id(event.getId())
                    .movieId(event.getMovieId())
                    .orderId(event.getOrderId())
                    .amount(event.getAmount())
                    .paymentMethod(event.getPaymentMethod())
                    .seats(event.getSeats())
                    .seatsStatus(objectMapper.writeValueAsString(seatsMap))
                    .clientIp(event.getClientIp())
                    .build();
            commandGateway.sendAndWait(updateMovieSeatsCommand);

        } catch (Exception ex) {
            rollbackCheckOutRecord(event.getId());
            log.error(ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "movieId")
    private void handler(MovieSeatsUpdatedEvent event) {
        log.info("MovieSeatsUpdatedEvent in Saga for movieId: " + event.getMovieId());
        try {
            String paymentUrl = generateVNPayUrl(event.getOrderId(), event.getAmount(), event.getClientIp());

            SagaLifecycle.associateWith("orderId", event.getOrderId());
            SavePaymentUrlCommand savePaymentUrlCommand = SavePaymentUrlCommand.builder()
                    .id(event.getId())
                    .orderId(event.getOrderId())
                    .paymentUrl(paymentUrl)
                    .build();
            commandGateway.sendAndWait(savePaymentUrlCommand);
        } catch (Exception ex) {
            // rollback Movie seats status
            rollbackMovieSeats(event.getId(), event.getMovieId(), event.getSeats());
            log.error(ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    private void handler(PaymentUrlSavedEvent event) {
        log.info("PaymentUrlSavedEvent in Saga for orderId: " + event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    private void handler(PaymentSucceededEvent event) {
        log.info("PaymentSucceededEvent in Saga for orderId: " + event.getOrderId());
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    private void handler(PaymentFailedEvent event) {
        log.info("PaymentFailedEvent in Saga for orderId: " + event.getOrderId());
        rollbackMovieSeats(event.getId(), event.getMovieId(), event.getSeats());
    }

    @SagaEventHandler(associationProperty = "movieId")
    private void handle(MovieSeatsRollbackedEvent event) {
        log.info("MovieSeatRollBackEvent in Saga for roll back movie Id : {} ", event.getMovieId());
        rollbackCheckOutRecord(event.getId());
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    private void handle(CheckOutDeletedEvent event) {
        log.info("CheckOutDeletedEvent in Saga for Checkout Payment Id : {} ", event.getId());
        SagaLifecycle.end();
    }

    private void rollbackMovieSeats(String paymentId, String movieId, String seats) {
        SagaLifecycle.associateWith("movieId", movieId);
        RollbackMovieSeatsCommand rollbackMovieSeatsCommand = RollbackMovieSeatsCommand.builder()
                .id(paymentId)
                .movieId(movieId)
                .seats(seats)
                .build();
        commandGateway.sendAndWait(rollbackMovieSeatsCommand);
    }

    private void rollbackCheckOutRecord(String id) {
        DeleteCheckOutCommand command = new DeleteCheckOutCommand(id);
        commandGateway.sendAndWait(command);
        SagaLifecycle.end();
    }

    private String generateVNPayUrl(String orderId, double amount, String clientIp) {
        long vnpAmount = (long) (amount * 100);
        Map<String, String> vnpParams = vnPayConfig.getVNPayConfig();
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Purchase ticket movie: " + orderId);
        vnpParams.put("vnp_IpAddr", clientIp);

        return VNPayUtils.getPaymentUrl(vnpParams, vnPayConfig.getHashSecret(), vnPayConfig.getUrl());
    }

}