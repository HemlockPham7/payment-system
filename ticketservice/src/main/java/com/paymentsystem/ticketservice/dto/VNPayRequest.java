package com.paymentsystem.ticketservice.dto;

public record VNPayRequest(
        String orderId,
        double amount,
        String ipAddress
) {
}