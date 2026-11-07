package com.example.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // Only the topic this service owns. order-events is created by the Order Service.
    @Bean
    NewTopic paymentEvents(@Value("${app.kafka.payment-topic}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
