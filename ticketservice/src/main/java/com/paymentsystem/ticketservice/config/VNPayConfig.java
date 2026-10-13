package com.paymentsystem.ticketservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class VNPayConfig {

    @Value("${vnpay.version}")
    private String version;
    @Value("${vnpay.command}")
    private String command;
    @Value("${vnpay.order-type}")
    private String orderType;
    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Getter
    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Getter
    @Value("${vnpay.url}")
    private String url;
    @Value("${vnpay.return-url}")
    private String returnUrl;

    public Map<String, String> getVNPayConfig () {
        Map<String, String> vnPayParams = new HashMap<>();
        vnPayParams.put("vnp_Version", this.version);
        vnPayParams.put("vnp_Command", this.command);
        vnPayParams.put("vnp_OrderType", this.orderType);
        vnPayParams.put("vnp_TmnCode", this.tmnCode);
        vnPayParams.put("vnp_CurrCode", "VND");
        vnPayParams.put("vnp_Locale", "vn");
        vnPayParams.put("vnp_ReturnUrl", this.returnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnPayParams.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnPayParams.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnPayParams;
    }
}
