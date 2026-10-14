package com.paymentsystem.ticketservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCheckOutCommand {

    @TargetAggregateIdentifier
    private String id;

    private String customerId;
    private String movieId;
    private String orderId;
    private String idempotencyKey;
    private Double amount;
    private String seats;
    private String paymentMethod;
    private String clientIp;
}