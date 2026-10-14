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
public class PaymentFailedCommand {


    @TargetAggregateIdentifier
    private String id;
    private String orderId;
    private String movieId;
    private String seats;
}
