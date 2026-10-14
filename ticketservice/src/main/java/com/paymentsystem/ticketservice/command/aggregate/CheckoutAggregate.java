package com.paymentsystem.ticketservice.command.aggregate;

import com.paymentsystem.ticketservice.command.command.*;
import com.paymentsystem.ticketservice.command.event.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class CheckoutAggregate {

    @AggregateIdentifier
    private String id;

    private String customerId;
    private String movieId;
    private String orderId;
    private String idempotencyKey;
    private Double amount;
    private String seats;
    private String seatsStatus;
    private String paymentMethod;
    private String clientIp;
    private String paymentUrl;

    public CheckoutAggregate() {}

    @CommandHandler
    public CheckoutAggregate(CreateCheckOutCommand command) {
        CheckOutCreatedEvent event = new CheckOutCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CheckOutCreatedEvent event) {
        this.id = event.getId();
        this.movieId = event.getMovieId();
        this.customerId = event.getCustomerId();
        this.orderId = event.getOrderId();
        this.idempotencyKey = event.getIdempotencyKey();
        this.paymentMethod = event.getPaymentMethod();
        this.amount = event.getAmount();
        this.seats = event.getSeats();
        this.clientIp = event.getClientIp();
    }


    @CommandHandler
    public void handle(DeleteCheckOutCommand command) {
        CheckOutDeletedEvent event = new CheckOutDeletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CheckOutDeletedEvent event) {
        this.id = event.getId();
    }

    @CommandHandler
    public void handle(UpdateMovieSeatsCommand command) {
        MovieSeatsUpdatedEvent event = new MovieSeatsUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(MovieSeatsUpdatedEvent event) {
        this.id = event.getId();
        this.movieId = event.getMovieId();
        this.orderId = event.getOrderId();
        this.paymentMethod = event.getPaymentMethod();
        this.amount = event.getAmount();
        this.seats = event.getSeats();
        this.seatsStatus = event.getSeatsStatus();
        this.clientIp = event.getClientIp();
    }

    @CommandHandler
    public void handle(RollbackMovieSeatsCommand command) {
        MovieSeatsRollbackedEvent event = new MovieSeatsRollbackedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(MovieSeatsRollbackedEvent event) {
        this.movieId = event.getMovieId();
        this.id = event.getId();
        this.seats = event.getSeats();
    }

    @CommandHandler
    public void handle(SavePaymentUrlCommand command) {
        PaymentUrlSavedEvent event = new PaymentUrlSavedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentUrlSavedEvent event) {
        this.orderId = event.getOrderId();
        this.id = event.getId();
        this.paymentUrl = event.getPaymentUrl();
    }

    @CommandHandler
    public void handle(PaymentSucceedCommand command) {
        PaymentSucceededEvent event = new PaymentSucceededEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentSucceededEvent event) {
        this.id = event.getId();
        this.orderId = event.getOrderId();
    }

    @CommandHandler
    public void handle(PaymentFailedCommand command) {
        PaymentFailedEvent event = new PaymentFailedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentFailedEvent event) {
        this.orderId = event.getOrderId();
        this.id = event.getId();
        this.movieId = event.getMovieId();
        this.seats = event.getSeats();
    }
}