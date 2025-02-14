package com.fatima.notification_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MetroService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getStationManagerEmail(Long stationId) {
        String url = "http://localhost:8093/stations/email/" + stationId;
        return restTemplate.getForObject(url, String.class);
//        return "fatima.amani65@gmail.com";
    }

    public String getStationName(Long stationId) {
        String url = "http://localhost:8093/stations/name/" + stationId;
        return restTemplate.getForObject(url, String.class);
//        return "fatima.amani65@gmail.com";
    }
}
