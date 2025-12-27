package com.example.aigenerate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.api")
@Data
public class AiApiConfig {
    private String url;
    private String key;
    private String model;
}