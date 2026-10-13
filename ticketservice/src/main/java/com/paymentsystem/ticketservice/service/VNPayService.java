package com.paymentsystem.ticketservice.service;

import com.paymentsystem.ticketservice.dto.VNPayRequest;

import java.util.Map;

public interface VNPayService {

    String createOrder(VNPayRequest request);
    int orderReturn(Map<String, String> params);
}
