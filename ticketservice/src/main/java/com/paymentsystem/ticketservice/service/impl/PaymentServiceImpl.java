package com.paymentsystem.ticketservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsystem.ticketservice.config.VNPayConfig;
import com.paymentsystem.ticketservice.dto.CheckoutRequest;
import com.paymentsystem.ticketservice.entity.Movie;
import com.paymentsystem.ticketservice.entity.Payment;
import com.paymentsystem.ticketservice.repository.MovieRepository;
import com.paymentsystem.ticketservice.repository.PaymentRepository;
import com.paymentsystem.ticketservice.service.PaymentService;
import com.paymentsystem.ticketservice.util.VNPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MovieRepository movieRepository;

    private final VNPayConfig vnPayConfig;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String processCheckout(CheckoutRequest request, String idempotencyKey, String clientIp) {
        // 1. Check idempotency key
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
            if (existingPayment.isPresent()) {
                log.info("Idempotency key is duplicated: {}. ", idempotencyKey);
                Payment payment = existingPayment.get();

                if (payment.getPaymentStatus() == Payment.PaymentStatus.PENDING &&
                        payment.getPaymentMethod() == Payment.PaymentMethod.VNPAY) {

                    return generateVNPayUrl(payment.getOrderId(), payment.getAmount(), clientIp);
                }
                throw new RuntimeException("Transaction with this idempotency key has completed");
            }
        }

        // 2. Get movie id and lock the seat
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new RuntimeException("Not found movie ID: " + request.movieId()));

        Map<String, String> seatsMap;
        try {
            seatsMap = objectMapper.readValue(movie.getSeatsStatus(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Mapping movie seat status fail!");
        }

        for (String seatCode : request.seats()) {
            String currentStatus = seatsMap.get(seatCode);
            if (currentStatus == null) {
                throw new RuntimeException("Seat " + seatCode + " not found!");
            }
            if (!"AVAILABLE".equals(currentStatus)) {
                throw new RuntimeException("Seat " + seatCode + " is not available!");
            }
            seatsMap.put(seatCode, "LOCKED");
        }

        // Update seat Movie (LOCKED)
        try {
            movie.setSeatsStatus(objectMapper.writeValueAsString(seatsMap));
            movieRepository.save(movie);
        } catch (Exception e) {
            throw new RuntimeException("Update movie status fail!");
        }

        // 3. Create Order ID and Payment
        String orderId = "DH" + System.currentTimeMillis();
        String seatsString = String.join(",", request.seats());

        Payment payment = Payment.builder()
                .orderId(orderId)
                .idempotencyKey(idempotencyKey)
                .amount(request.amount())
                .customerId(request.customerId())
                .seats(seatsString)
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .paymentMethod(Payment.PaymentMethod.valueOf(request.paymentMethod().toUpperCase()))
                .build();
        paymentRepository.save(payment);

        if ("VNPAY".equalsIgnoreCase(request.paymentMethod())) {
            return generateVNPayUrl(orderId, request.amount(), clientIp);
        }

        return "Successfully purchase order id: " + orderId;
    }

    private String generateVNPayUrl(String orderId, double amount, String clientIp) {
        long vnpAmount = (long) (amount * 100);
        Map<String, String> vnpParams = vnPayConfig.getVNPayConfig();
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Purchase ticket movie: " + orderId);
        vnpParams.put("vnp_IpAddr", clientIp);

        return VNPayUtils.getPaymentUrl(vnpParams, vnPayConfig.getHashSecret(), vnPayConfig.getUrl());
    }

    @Override
    @Transactional
    public int processVNPayCallback(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");

        // 1. Check if the signature is correct
        boolean checkSign = VNPayUtils.verifySignature(params, vnPayConfig.getHashSecret(), vnp_SecureHash);
        if (!checkSign) {
            log.error("VNPay Callback: Invalid signature!");
            return -1;
        }

        // 2. Find (orderId) from vnp_TxnRef
        String orderId = params.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElse(null);

        if (payment == null) {
            log.error("VNPay Callback: Order not found for orderId: {}", orderId);
            return 0;
        }

        // If the payment has already status = success then next
        if (payment.getPaymentStatus() == Payment.PaymentStatus.SUCCESS) {
            return 1;
        }

        // 3. check the returned transactional code (vnp_ResponseCode == "00" is success)
        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            log.info("Purchase successfully: {}", orderId);
            return 1;
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Purchase fail: {}, ResponseCode: {}", orderId, responseCode);
            return 0;
        }
    }
}
