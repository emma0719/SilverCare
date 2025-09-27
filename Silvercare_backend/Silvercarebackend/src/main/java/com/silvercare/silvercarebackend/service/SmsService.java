package com.silvercare.silvercarebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmsService {

    private final RestTemplate restTemplate;
    private final String infobipApiUrl;
    private final String infobipApiKey;
    private final String sender;

    public SmsService(RestTemplate restTemplate,
                      @Value("${infobip.base-url}") String infobipApiUrl,
                      @Value("${infobip.api-key}") String infobipApiKey,
                      @Value("${infobip.from}") String sender) {
        this.restTemplate = restTemplate;
        this.infobipApiUrl = infobipApiUrl;
        this.infobipApiKey = infobipApiKey;
        this.sender = sender;
    }

    public String sendSms(String to, String message) {
        String endpoint = infobipApiUrl + "/sms/2/text/advanced";

        // 清理电话号码，避免前后空格
        String cleanNumber = to.trim();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", infobipApiKey);

        Map<String, Object> smsRequest = new HashMap<>();
        smsRequest.put("messages", Collections.singletonList(Map.of(
                "from", sender,
                "destinations", Collections.singletonList(Map.of("to", cleanNumber)),
                "text", message
        )));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(smsRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.info("✅ SMS sent to {} | Response: {}", cleanNumber, response.getBody());
            return response.getBody();

        } catch (RestClientException e) {
            log.error("❌ Failed to send SMS to {} | Error: {}", cleanNumber, e.getMessage(), e);
            return "{\"error\":\"Failed to send SMS\"}";
        }
    }
}
