package com.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessKafkaEvent {

    private String transactionId;
    private BigDecimal amount;
    private String paymentType;
    private String email;
}