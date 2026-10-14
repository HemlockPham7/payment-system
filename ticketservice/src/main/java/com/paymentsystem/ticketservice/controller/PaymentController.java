package com.paymentsystem.ticketservice.controller;

import com.paymentsystem.ticketservice.dto.CheckoutRequest;
import com.paymentsystem.ticketservice.service.CheckOutService;
import com.paymentsystem.ticketservice.util.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${vnpay.frontend-url}")
    private String frontendUrl;

    private final CheckOutService checkOutService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestBody CheckoutRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            HttpServletRequest httpRequest
    ) {

        String clientIp = VNPayUtils.getIpAddress(httpRequest);
        String paymentId = checkOutService.processCheckout(request, idempotencyKey, clientIp);

        return ResponseEntity.ok(paymentId);
    }

    @GetMapping("/vnpay-callback")
    public void payCallback(
            @RequestParam Map<String, String> queryParams,
            HttpServletResponse response
    ) throws IOException {
        int paymentStatus = checkOutService.processVNPayCallback(queryParams);

        String orderId = queryParams.get("vnp_TxnRef");
        String amount = queryParams.get("vnp_Amount");
        String redirectFrontendUrl = frontendUrl + "?orderId=" + orderId + "&amount=" + amount + "&status=";

        if (paymentStatus == 1) {
            response.sendRedirect(redirectFrontendUrl + "success");
        } else if (paymentStatus == -1) {
            response.sendRedirect(redirectFrontendUrl + "invalid_signature");
        } else {
            response.sendRedirect(redirectFrontendUrl + "failed");
        }
    }

//    @PostMapping("/checkout")
//    public ResponseEntity<String> checkoutV1(
//            @RequestBody CheckoutRequest request,
//            @RequestHeader("Idempotency-Key") String idempotencyKey,
//            HttpServletRequest httpRequest
//    ) {
//
//        String clientIp = VNPayUtils.getIpAddress(httpRequest);
//        String paymentUrl = paymentService.processCheckout(request, idempotencyKey, clientIp);
//
//        return ResponseEntity.ok(paymentUrl);
//    }
}
