package com.example.aigenerate.service;


import com.example.aigenerate.dto.VideoSynthesisDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class TaskQueryService {

    @Value("${aliyun.apiKey}")
    private String apiKey;

    @Value("${aliyun.dashscope.query-task-url}")
    private String apiUrl;

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public TaskQueryService(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    public VideoSynthesisDto.Response queryTask(String taskId) throws IOException {
        String url = apiUrl + "/" + taskId;
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                throw new IOException("Query failed: " + response.code() + " - " + response.body().string());
            }
            assert response.body() != null;
            return objectMapper.readValue(response.body().string(), VideoSynthesisDto.Response.class);
        }
    }
}