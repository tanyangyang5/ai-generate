package com.example.aigenerate.service;


import com.example.aigenerate.dto.VideoSynthesisDto;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class VideoSynthesisService {

    @Value("${aliyun.dashscope.video-synthesis-url}")
    private String apiUrl ;

    @Value("${aliyun.apiKey}")
    private String apiKey;

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public VideoSynthesisService(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    public VideoSynthesisDto.Response submitTask(VideoSynthesisDto.Request request) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request httpRequest = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("X-DashScope-Async", "enable")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Submit failed: " + response.code() + " - " + response.body().string());
            }
            return objectMapper.readValue(response.body().string(), VideoSynthesisDto.Response.class);
        }
    }


}