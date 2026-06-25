package com.notification_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDltConsumer {

    @KafkaListener(
            topics = "${payment.kafka.topic.payment-success-dlt}",
            groupId = "notification-dlt-group"
    )
    public void consumeFailedPaymentEvent(String message) {

        System.out.println("========== DLT MESSAGE RECEIVED ==========");
        System.out.println("Payment email failed after retries");
        System.out.println("Failed Kafka message: " + message);
        System.out.println("==========================================");
    }
}