package com.paymentsystem.ticketservice.dto;

import java.util.List;

public record CheckoutRequest(
        String customerId,
        String movieId,
        Double amount,
        List<String> seats,
        String paymentMethod // "VNPAY" or "CASH"
) {
}