package com.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class PaymentDltConsumerTest {

    @InjectMocks
    private PaymentDltConsumer paymentDltConsumer;

    private String dltMessage;

    @BeforeEach
    void setUp() {
        dltMessage = "{\"transactionId\":\"txn123\",\"amount\":100.00,\"paymentType\":\"CREDIT_CARD\",\"email\":\"fail@test.com\"}";
    }

    @Test
    void consumeFailedPaymentEvent_Success() {
        assertDoesNotThrow(() -> paymentDltConsumer.consumeFailedPaymentEvent(dltMessage));
    }

    @Test
    void consumeFailedPaymentEvent_NullMessage() {
        assertDoesNotThrow(() -> paymentDltConsumer.consumeFailedPaymentEvent(null));
    }

    @Test
    void consumeFailedPaymentEvent_EmptyMessage() {
        assertDoesNotThrow(() -> paymentDltConsumer.consumeFailedPaymentEvent(""));
    }
}
