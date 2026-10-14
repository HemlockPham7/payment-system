package com.paymentsystem.ticketservice.service.impl;

import com.paymentsystem.ticketservice.command.command.CreateCheckOutCommand;
import com.paymentsystem.ticketservice.command.command.PaymentFailedCommand;
import com.paymentsystem.ticketservice.command.command.PaymentSucceedCommand;
import com.paymentsystem.ticketservice.config.VNPayConfig;
import com.paymentsystem.ticketservice.dto.CheckoutRequest;
import com.paymentsystem.ticketservice.entity.Payment;
import com.paymentsystem.ticketservice.repository.PaymentRepository;
import com.paymentsystem.ticketservice.service.CheckOutService;
import com.paymentsystem.ticketservice.util.VNPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckOutServiceImpl implements CheckOutService {

    private final PaymentRepository paymentRepository;

    private final CommandGateway commandGateway;
    private final VNPayConfig vnPayConfig;

    @Override
    public String processCheckout(CheckoutRequest request, String idempotencyKey, String clientIp) {
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

//        String orderId = "DH" + System.currentTimeMillis();
        String seatsString = String.join(",", request.seats());

        String paymentId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();

        CreateCheckOutCommand createCheckOutCommand = CreateCheckOutCommand.builder()
                .id(paymentId)
                .movieId(request.movieId())
                .orderId(orderId)
                .customerId(request.customerId())
                .idempotencyKey(idempotencyKey)
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .seats(seatsString)
                .clientIp(clientIp)
                .build();
        commandGateway.sendAndWait(createCheckOutCommand);

        return paymentId;
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
    public int processVNPayCallback(Map<String, String> queryParams) {
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");

        // 1. Check if the signature is correct
        boolean checkSign = VNPayUtils.verifySignature(queryParams, vnPayConfig.getHashSecret(), vnp_SecureHash);
        if (!checkSign) {
            log.error("VNPay Callback: Invalid signature!");
            return -1;
        }

        // 2. Find (orderId) from vnp_TxnRef
        String orderId = queryParams.get("vnp_TxnRef");
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
        String responseCode = queryParams.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            PaymentSucceedCommand command = PaymentSucceedCommand.builder()
                    .id(payment.getId())
                    .orderId(orderId)
                    .build();
            commandGateway.sendAndWait(command);
            log.info("Purchase successfully: {}", orderId);
            return 1;
        } else {
            PaymentFailedCommand command = PaymentFailedCommand.builder()
                    .orderId(orderId)
                    .id(payment.getId())
                    .seats(payment.getSeats())
                    .movieId(payment.getMovieId())
                    .build();
            commandGateway.sendAndWait(command);
            log.warn("Purchase fail: {}, ResponseCode: {}", orderId, responseCode);
            return 0;
        }
    }
}
