package com.fatima.notification_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUserEmail(Long userId) {
        String url = "http://localhost:8080/user/" + userId;
        return restTemplate.getForObject(url, String.class);
//        return "fatima.amani65@gmail.com";
    }
}
