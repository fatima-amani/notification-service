package com.fatima.notification_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MetroService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getStationManagerEmail(Long stationId) {
//        String url = "http://METRO-SERVICE/stations/" + stationId + "/manager-email";
//        return restTemplate.getForObject(url, String.class);
        return "fatima.amani65@gmail.com";
    }
}
