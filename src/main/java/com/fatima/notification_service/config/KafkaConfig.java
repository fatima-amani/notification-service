package com.fatima.notification_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentSuccessTopic() {
        return new NewTopic("ticket_payment_success", 1, (short) 1);
    }

    @Bean
    public NewTopic penaltyChargedTopic() {
        return new NewTopic("penalty_charged", 1, (short) 1);
    }

    @Bean
    public NewTopic sosAlertTopic() {
        return new NewTopic("sos_alert", 1, (short) 1);
    }
}
