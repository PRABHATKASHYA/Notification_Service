package com.notification_service.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${payment.kafka.topic.payment-success-dlt}")
            String paymentSuccessDltTopic
    ){
        DeadLetterPublishingRecoverer recoverer= new DeadLetterPublishingRecoverer(
                kafkaTemplate,(record, exception)->new TopicPartition(
                        paymentSuccessDltTopic, record.partition()
        )
        );
        return new DefaultErrorHandler(
                recoverer, new FixedBackOff(5000L ,2L)
        );
    }
}
