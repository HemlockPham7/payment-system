package com.paymentsystem.ticketservice.service.impl;

import com.paymentsystem.ticketservice.entity.Order;
import com.paymentsystem.ticketservice.repository.OrderRepository;
import com.paymentsystem.ticketservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public String getPaymentUrl(String paymentId) {
        return orderRepository.findByPaymentId(paymentId)
                .map(Order::getPaymentUrl)
                .orElse("PAYMENT_URL_NOT_READY");
    }
}
