package com.riya.dentalbot.whatsapp.client;

import com.riya.dentalbot.whatsapp.config.WhatsAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhatsAppClient {

    private final RestTemplate restTemplate;
    private final WhatsAppProperties properties;

    public void sendTextMessage(String phoneNumberId, String to, String messageText) {

        if (phoneNumberId == null) {
            log.warn("Skipping WhatsApp send — clinic has no whatsappPhoneNumberId configured");
            return;
        }

        String url = "%s/%s/%s/messages".formatted(
                properties.getGraphBaseUrl(),
                properties.getApiVersion(),
                phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getAccessToken());

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", messageText));

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        } catch (Exception ex) {
            log.error("Failed to send WhatsApp message to {}: {}", to, ex.getMessage());
        }
    }
}