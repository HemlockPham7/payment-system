package com.paymentsystem.ticketservice.service;

import com.paymentsystem.ticketservice.dto.CheckoutRequest;

import java.util.Map;

public interface CheckOutService {

    String processCheckout(CheckoutRequest request, String idempotencyKey, String clientIp);
    int processVNPayCallback(Map<String, String> queryParams);
}
