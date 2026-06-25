package com.notification_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification_service.dto.EmailRequest;
import com.notification_service.dto.PaymentSuccessKafkaEvent;
import com.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(
            topics = "${payment.kafka.topic.payment-success}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePaymentSuccessEvent(
            String message
    ) {

        try {
            PaymentSuccessKafkaEvent event =
                    objectMapper.readValue(
                            message,
                            PaymentSuccessKafkaEvent.class
                    );

            if (event.getEmail().equalsIgnoreCase("fail@test.com")) {
                throw new RuntimeException(
                        "Testing Kafka retry and DLT flow"
                );
            }

            System.out.println(
                    "Kafka payment event received: "
                            + event.getTransactionId()
            );

            EmailRequest emailRequest = new EmailRequest();

            emailRequest.setTo(event.getEmail());
            emailRequest.setSubject("Payment Successful");

            emailRequest.setMessage(
                    "Your payment was successful.\n\n" +
                            "Transaction Id: " + event.getTransactionId() +
                            "\nAmount: Rs. " + event.getAmount() +
                            "\nPayment Type: " + event.getPaymentType()
            );

            emailService.sendEmail(emailRequest);

            System.out.println(
                    "Payment success email sent"
            );

        } catch (JsonProcessingException exception) {

            throw new RuntimeException(
                    "Kafka message JSON conversion failed",
                    exception
            );
        }
    }
}