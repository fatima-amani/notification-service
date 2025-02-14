package com.fatima.notification_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fatima.notification_service.service.EmailService;
import com.fatima.notification_service.service.UserService;
import com.fatima.notification_service.service.MetroService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;
    private final UserService userService;
    private final MetroService metroService;

    @KafkaListener(topics = "ticket_payment_success", groupId = "notification-group")
    public void handlePaymentSuccess(Map<String, Object> eventData) {
        Long userId = Long.valueOf(eventData.get("userId").toString());
        String email = userService.getUserEmail(userId);
        String message = "Your metro ticket from station " + eventData.get("source") +
                " to " + eventData.get("destination") + " has been purchased successfully.";
        emailService.sendEmail(email, "Metro Ticket Purchase", message);
    }

    @KafkaListener(topics = "penalty_charged", groupId = "notification-group")
    public void handlePenalty(Map<String, Object> eventData) {
        Long userId = Long.valueOf(eventData.get("userId").toString());
        String email = userService.getUserEmail(userId);
        String message = "A penalty of Rs. " + eventData.get("penaltyAmount") + " has been charged to your account.";
        emailService.sendEmail(email, "Penalty Alert", message);
    }

    @KafkaListener(topics = "sos_alert", groupId = "notification-group")
    public void handleSOS(@Payload String message) {
        System.out.println("Received raw message: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});
            System.out.println("Parsed SOS Data: " + data);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
