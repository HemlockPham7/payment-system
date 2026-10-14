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
public class UpdateMovieSeatsCommand {

    @TargetAggregateIdentifier
    private String id;
    private String movieId;
    private String orderId;
    private Double amount;
    private String seats;
    private String seatsStatus;
    private String paymentMethod;
    private String clientIp;
}
