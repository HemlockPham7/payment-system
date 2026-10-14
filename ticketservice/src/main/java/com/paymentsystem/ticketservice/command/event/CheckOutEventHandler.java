package com.paymentsystem.ticketservice.command.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsystem.ticketservice.entity.Movie;
import com.paymentsystem.ticketservice.entity.Order;
import com.paymentsystem.ticketservice.entity.Payment;
import com.paymentsystem.ticketservice.repository.MovieRepository;
import com.paymentsystem.ticketservice.repository.OrderRepository;
import com.paymentsystem.ticketservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class CheckOutEventHandler {

    private final PaymentRepository paymentRepository;
    private final MovieRepository movieRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @EventHandler
    public void on(CheckOutCreatedEvent event) {
        Payment payment = Payment.builder()
                .id(event.getId())
                .idempotencyKey(event.getIdempotencyKey())
                .orderId(event.getOrderId())
                .amount(event.getAmount())
                .movieId(event.getMovieId())
                .customerId(event.getCustomerId())
                .seats(event.getSeats())
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .paymentMethod(Payment.PaymentMethod.valueOf(event.getPaymentMethod().toUpperCase()))
                .build();
        paymentRepository.save(payment);
        log.info("Process CheckOutCreatedEvent");
    }

    @EventHandler
    public void on(CheckOutDeletedEvent event) {
        Optional<Payment> oldPayment = paymentRepository.findById(event.getId());
        oldPayment.ifPresent(paymentRepository::delete);
        log.info("Process CheckOutDeletedEvent");
    }

    @EventHandler
    public void on(MovieSeatsUpdatedEvent event) {
        Optional<Movie> oldMovieStatus = movieRepository.findById(event.getMovieId());
        oldMovieStatus.ifPresent(movie -> {
            movie.setSeatsStatus(event.getSeatsStatus());
            movieRepository.save(movie);
        });
        log.info("Process MovieSeatsUpdatedEvent");
    }

    @EventHandler
    public void on(MovieSeatsRollbackedEvent event) {

        Optional<Movie> oldMovieStatus = movieRepository.findById(event.getMovieId());
        oldMovieStatus.ifPresent(movie -> {
            Map<String, String> seatsMap;
            try {
                seatsMap = objectMapper.readValue(oldMovieStatus.get().getSeatsStatus(), new TypeReference<>() {});
            } catch (Exception e) {
                throw new RuntimeException("Mapping movie seat status fail!");
            }
            String seatCodes = event.getSeats();
            for (String seatCode : seatCodes.split(",")) {
                seatCode = seatCode.trim();
                seatsMap.put(seatCode, "AVAILABLE");
            }
            try {
                movie.setSeatsStatus(objectMapper.writeValueAsString(seatsMap));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            movieRepository.save(movie);
        });
        log.info("Process MovieSeatsRollbackedEvent");
    }

    @EventHandler
    public void on(PaymentUrlSavedEvent event) {
        Order order = Order.builder()
                .id(event.getOrderId())
                .paymentId(event.getId())
                .paymentUrl(event.getPaymentUrl())
                .build();
        orderRepository.save(order);
        log.info("Process PaymentUrlSavedEvent");
    }

    @EventHandler
    public void on(PaymentSucceededEvent event) {
        Optional<Payment> oldPaymentStatus = paymentRepository.findByOrderId(event.getOrderId());
        oldPaymentStatus.ifPresent(payment -> {
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
        });
        log.info("Process PaymentSucceededEvent");
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        Optional<Payment> oldPaymentStatus = paymentRepository.findByOrderId(event.getOrderId());
        oldPaymentStatus.ifPresent(payment -> {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
        log.info("Process PaymentFailedEvent");
    }
}
