package com.riya.dentalbot.whatsapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "whatsapp")
@Getter
@Setter
public class WhatsAppProperties {

    private String accessToken;

    private String verifyToken;

    private String apiVersion;

    private String graphBaseUrl;

}