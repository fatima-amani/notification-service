package com.fatima.notification_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final UserService userService; // To get user emails
    private final MetroService metroService; // To get station manager emails

    @KafkaListener(topics = "ticket_payment_success", groupId = "notification-group")
    public void handlePaymentSuccess(String eventData) {
        // Convert eventData (JSON String) to Map
        Map<String, Object> event = parseJson(eventData);
        Long userId = Long.valueOf(event.get("userId").toString());
        Double amount = Double.valueOf(event.get("amount").toString());
        String source = event.get("source").toString();
        String destination = event.get("destination").toString();

        // Fetch user email
        String userEmail = userService.getUserEmail(userId);

        // Send payment success email
        String subject = "Metro Ticket Purchase Confirmation";
        String body = String.format("Dear User, <br> Your ticket from %s to %s was purchased successfully. <br> Amount: ₹%.2f", source, destination, amount);

        emailService.sendEmail(userEmail, subject, body);
    }

    @KafkaListener(topics = "penalty_charged", groupId = "notification-group")
    public void handlePenaltyCharged(String eventData) {
        Map<String, Object> event = parseJson(eventData);
        Long userId = Long.valueOf(event.get("userId").toString());
        Double penaltyAmount = Double.valueOf(event.get("penaltyAmount").toString());

        // Fetch user email
        String userEmail = userService.getUserEmail(userId);

        // Send penalty email
        String subject = "Penalty Charged - Metro Service";
        String body = String.format("Dear User, <br> A penalty of ₹%.2f has been charged due to travel time exceeding limits.", penaltyAmount);

        emailService.sendEmail(userEmail, subject, body);
    }

    @KafkaListener(topics = "sos_alert", groupId = "notification-group")
    public void handleSOSAlert(String eventData) {
        Map<String, Object> event = parseJson(eventData);
        Long userId = Long.valueOf(event.get("userId").toString());
        Long stationId = Long.valueOf(event.get("stationId").toString());

        // Fetch station manager email
        String managerEmail = metroService.getStationManagerEmail(stationId);

        // Send SOS alert email
        String subject = "URGENT: SOS Alert at Your Station";
        String body = "An emergency SOS alert has been triggered by a passenger. Please respond immediately.";

        emailService.sendEmail(managerEmail, subject, body);
    }

    // Convert JSON string to Map
    private Map<String, Object> parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}

