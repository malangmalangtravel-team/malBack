package com.malback.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OAuth2LogConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @PostConstruct
    public void logOAuthInfo() {
        log.info("✅ [OAuth2 설정 확인]");
        log.info("Google Client ID: {}", googleClientId);
        log.info("Google Redirect URI: {}", googleRedirectUri);
    }
}

