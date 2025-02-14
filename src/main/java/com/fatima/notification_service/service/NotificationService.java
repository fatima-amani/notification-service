package com.fatima.notification_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final UserService userService;
    private final MetroService metroService;

    @KafkaListener(topics = "ticket_payment_success", groupId = "notification-group")
    public void handlePaymentSuccess(String eventData) {
        log.info("Payment Success Mail sent");
//        System.out.println("Received ticket_payment_success event: " + eventData);

        Map<String, Object> event = parseJson(eventData);
        if (event == null) return;

        // Use the correct keys from the received JSON message
        String userId = event.get("userId").toString();
        String source = metroService.getStationName(Long.parseLong(event.get("src").toString()));
        String destination = metroService.getStationName(Long.parseLong(event.get("dest").toString()));
        String amount = event.get("amount").toString();

        String userEmail = userService.getUserEmail(Long.valueOf(userId));

        String subject = "Metro Ticket Purchase Confirmation";
        String body = "Dear User, <br> Your ticket from " + source + " to " + destination +
                " was purchased successfully. <br> Amount: ₹" + amount;

        emailService.sendEmail(userEmail, subject, body);
    }


    @KafkaListener(topics = "penalty_charged", groupId = "notification-group")
    public void handlePenaltyCharged(String eventData) {
        log.info("Penalty Mail sent");
//        System.out.println("Received penalty_charged event: " + eventData);

        Map<String, Object> event = parseJson(eventData);
        if (event == null) return;

        String userId = event.get("userId").toString();
        String penaltyAmount = event.get("penaltyAmount").toString();

        String userEmail = userService.getUserEmail(Long.valueOf(userId));

        String subject = "Penalty Charged - Metro Service";
        String body = "Dear User, <br> A penalty of ₹" + Float.parseFloat(penaltyAmount) + " has been charged to your account for exceeding a 90 minute limit.";

        emailService.sendEmail(userEmail, subject, body);
    }

    @KafkaListener(topics = "sos_alert", groupId = "notification-group")
    public void handleSOSAlert(String eventData) {
        log.info("Received SOS alert");
//        System.out.println("Received SOS alert event: " + eventData);

        Map<String, Object> event = parseJson(eventData);
        if (event == null) return;

        String stationId = event.get("stationId").toString();
        String managerEmail = metroService.getStationManagerEmail(Long.valueOf(stationId));

        String subject = "URGENT: SOS Alert at Your Station";
        String body = "An emergency SOS alert has been triggered by a passenger. Please respond immediately.";

        emailService.sendEmail(managerEmail, subject, body);
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + json);
            e.printStackTrace();
            return null;
        }
    }
}
