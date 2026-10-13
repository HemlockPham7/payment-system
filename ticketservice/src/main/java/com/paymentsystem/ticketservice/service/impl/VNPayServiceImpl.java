package com.paymentsystem.ticketservice.service.impl;

import com.paymentsystem.ticketservice.config.VNPayConfig;
import com.paymentsystem.ticketservice.dto.VNPayRequest;
import com.paymentsystem.ticketservice.service.VNPayService;
import com.paymentsystem.ticketservice.util.VNPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnPayConfig;

    @Override
    public String createOrder(VNPayRequest request) {
        long amount = (long) (request.amount() * 100);

        Map<String, String> vnpParams = vnPayConfig.getVNPayConfig();
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_TxnRef", request.orderId());
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + request.orderId());
        vnpParams.put("vnp_IpAddr", request.ipAddress());

        return VNPayUtils.getPaymentUrl(vnpParams, vnPayConfig.getHashSecret(), vnPayConfig.getUrl());
    }

    @Override
    public int orderReturn(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        boolean checkSign = VNPayUtils.verifySignature(params, vnPayConfig.getHashSecret(), vnp_SecureHash);

        if (!checkSign) {
            return -1; // Sign failed
        }

        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return 1; // Success
        }
        return 0; // Fail
    }
}
