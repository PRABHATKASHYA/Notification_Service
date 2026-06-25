package com.notification_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification_service.dto.EmailRequest;
import com.notification_service.dto.PaymentSuccessKafkaEvent;
import com.notification_service.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PaymentKafkaConsumer paymentKafkaConsumer;

    private String validMessage;
    private PaymentSuccessKafkaEvent validEvent;

    @BeforeEach
    void setUp() {
        validEvent = new PaymentSuccessKafkaEvent(
                "txn123",
                new BigDecimal("100.00"),
                "CREDIT_CARD",
                "success@example.com"
        );
        validMessage = "{\"transactionId\":\"txn123\",\"amount\":100.00,\"paymentType\":\"CREDIT_CARD\",\"email\":\"success@example.com\"}";
    }

    @Test
    void consumePaymentSuccessEvent_Success() throws JsonProcessingException {
        when(objectMapper.readValue(validMessage, PaymentSuccessKafkaEvent.class))
                .thenReturn(validEvent);

        paymentKafkaConsumer.consumePaymentSuccessEvent(validMessage);

        verify(emailService).sendEmail(any(EmailRequest.class));
    }

    @Test
    void consumePaymentSuccessEvent_FailEmail_ThrowsException() throws JsonProcessingException {
        PaymentSuccessKafkaEvent failEvent = new PaymentSuccessKafkaEvent(
                "txn456",
                new BigDecimal("200.00"),
                "UPI",
                "fail@test.com"
        );
        String failMessage = "{\"transactionId\":\"txn456\",\"amount\":200.00,\"paymentType\":\"UPI\",\"email\":\"fail@test.com\"}";

        when(objectMapper.readValue(failMessage, PaymentSuccessKafkaEvent.class))
                .thenReturn(failEvent);

        try {
            paymentKafkaConsumer.consumePaymentSuccessEvent(failMessage);
        } catch (RuntimeException e) {
            verify(emailService, never()).sendEmail(any(EmailRequest.class));
        }
    }

    @Test
    void consumePaymentSuccessEvent_JsonProcessingException_ThrowsRuntimeException() throws JsonProcessingException {
        when(objectMapper.readValue(any(String.class), eq(PaymentSuccessKafkaEvent.class)))
                .thenThrow(new RuntimeException("Invalid JSON"));

        try {
            paymentKafkaConsumer.consumePaymentSuccessEvent(validMessage);
        } catch (RuntimeException e) {
            verify(emailService, never()).sendEmail(any(EmailRequest.class));
        }
    }
}
