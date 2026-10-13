package com.paymentsystem.ticketservice.controller;

import com.paymentsystem.ticketservice.dto.VNPayRequest;
import com.paymentsystem.ticketservice.service.VNPayService;
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
@RequestMapping("/api/v1/vnpaytest")
@RequiredArgsConstructor
public class VNPayTestController {

    @Value("${vnpay.frontend-url}")
    private String frontendUrl;

    private final VNPayService vnPayService;

    // API create VNPAY link to check out
    @PostMapping("/submit-order")
    public ResponseEntity<String> submidOrder(
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") double amount,
            @RequestParam(value = "ipAddress", required = false) String ipAddress,
            HttpServletRequest httpRequest
    ) {
        // Automatically take the ip address if request does not have ip
        String clientIp = (ipAddress == null || ipAddress.isEmpty())
                ? VNPayUtils.getIpAddress(httpRequest)
                : ipAddress;

        VNPayRequest paymentRequest = new VNPayRequest(
                orderId,
                amount,
                clientIp
        );
        String paymentUrl = vnPayService.createOrder(paymentRequest);

        return ResponseEntity.ok(paymentUrl);
    }

    // API receives returned data from vnpay
    @GetMapping("/vnpay-callback")
    public void payCallback(
            @RequestParam Map<String, String> queryParams,
            HttpServletResponse response
    ) throws IOException {
        int paymentStatus = vnPayService.orderReturn(queryParams);

        String orderId = queryParams.get("vnp_TxnRef");

        String redirectFrontendUrl = frontendUrl + "?orderId=" + orderId + "&status=";

        if (paymentStatus == 1) {
            response.sendRedirect(redirectFrontendUrl + "success");
        } else {
            response.sendRedirect(redirectFrontendUrl + "failed");
        }
    }
}
