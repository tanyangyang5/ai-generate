package com.example.aigenerate.service;

import com.example.aigenerate.dto.VeoGenerationRequest;
import com.example.aigenerate.response.video.VeoVideoGenerationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VeoVideoGenerationService {

    @Value("${ezlinkai.api.commonVideoUrl}")
    private String apiUrl; // 应为 https://api.ezlinkai.com/v1/video/generations

    @Value("${ezlinkai.api.key}")
    private String apiKey;

    // 使用 OkHttp 客户端（可复用，线程安全）
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    public VeoVideoGenerationResponse generateVideo(VeoGenerationRequest request) {
        try {
            // 序列化请求体
            String jsonBody = objectMapper.writeValueAsString(request);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = okHttpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.info(response.body().string());
                    log.error("Veo API error: {} - {}", response.code(), response.message());
                    throw new RuntimeException("Veo generation failed: " + response.code());
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new RuntimeException("Empty response from Veo API");
                }

                String json = responseBody.string();
                log.info("Veo API response: {}", json);
                return objectMapper.readValue(json, VeoVideoGenerationResponse.class);
            }

        } catch (IOException e) {
            log.error("I/O error during Veo video generation", e);
            throw new RuntimeException("Failed to call Veo API", e);
        }
    }
}