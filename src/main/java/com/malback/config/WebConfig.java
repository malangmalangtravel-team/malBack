package com.malback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3030")  // Vue.js 프론트엔드 주소 (http://localhost:3030)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}