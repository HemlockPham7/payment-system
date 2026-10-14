package com.paymentsystem.ticketservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieSeatsUpdatedEvent {

    private String movieId;

    private String id;
    private String orderId;
    private Double amount;
    private String seats;
    private String seatsStatus;
    private String paymentMethod;
    private String clientIp;
}
