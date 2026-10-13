package com.paymentsystem.ticketservice.service;

import com.paymentsystem.ticketservice.dto.CheckoutRequest;

import java.util.Map;

public interface PaymentService {

    String processCheckout(CheckoutRequest request, String clientIp);
    int processVNPayCallback(Map<String, String> queryParams);
}
