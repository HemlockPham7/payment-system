package com.paymentsystem.ticketservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedEvent {

    private String orderId;

    private String id;
    private String movieId;
    private String seats;
}
