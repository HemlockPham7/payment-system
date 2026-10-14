package com.paymentsystem.ticketservice.controller;

import com.paymentsystem.ticketservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<String> getMovieDetails(@PathVariable String paymentId) {
        return ResponseEntity.ok(orderService.getPaymentUrl(paymentId));
    }
}
