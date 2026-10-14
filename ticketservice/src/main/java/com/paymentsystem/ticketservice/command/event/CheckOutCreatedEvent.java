package com.paymentsystem.ticketservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutCreatedEvent {

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