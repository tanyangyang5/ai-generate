package com.example.aigenerate.service;

import com.example.aigenerate.dto.MultiImageVideoSynthesisRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MultiImageVideoSynthesisService {
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/video-generation/video-synthesis";
    private static final String MODEL = "wanx2.1-vace-plus";

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public MultiImageVideoSynthesisService(OkHttpClient okHttpClient, ObjectMapper objectMapper, @Value("${aliyun.apiKey}") String apiKey) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public String submitTask(MultiImageVideoSynthesisRequest request) throws IOException {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);

        // 构建input对象
        Map<String, Object> input = new HashMap<>();
        input.put("function", "image_reference");
        input.put("prompt", request.getPrompt());
        input.put("ref_images_url", request.getRefImagesUrl());

        // 构建parameters对象
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true);
        parameters.put("size", "1280*720");

        // 如果提供了obj_or_bg参数，则添加到parameters中
        if (request.getObjOrBg() != null && !request.getObjOrBg().isEmpty()) {
            parameters.put("obj_or_bg", request.getObjOrBg());
        }

        requestBody.put("input", input);
        requestBody.put("parameters", parameters);

        // 序列化请求体
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // 创建请求体
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        // 构建HTTP请求
        Request httpRequest = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("X-DashScope-Async", "enable")
                .post(body)
                .build();

        // 执行请求
        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("API request failed: " + response.code() + " - " + errorBody);
            }

            // 返回响应体字符串
            return response.body() != null ? response.body().string() : "{}";
        }
    }





    private static final int MAX_RETRIES = 30;
    private static final long POLL_INTERVAL = 10000;

    public String generateVideoWithWait(MultiImageVideoSynthesisRequest request) throws IOException, InterruptedException {
        String submitResponse = submitTask(request);
        String taskId = parseTaskId(submitResponse);

        log.info("Task ID: {}", taskId);

        for (int i = 0; i < MAX_RETRIES; i++) {
            String statusResponse = queryTaskStatus(taskId);
            String status = parseTaskStatus(statusResponse);

            log.info("Task status: {}", status);

            if ("SUCCEEDED".equals(status)) {
                log.info("Task SUCCEEDED {}", statusResponse);
                return statusResponse;
            } else if ("FAILED".equals(status)) {
                throw new IOException("Task failed: " + statusResponse);
            }

            Thread.sleep(POLL_INTERVAL);
        }

        throw new IOException("Task timeout after " + MAX_RETRIES * 10 + " seconds");
    }

    private String queryTaskStatus(String taskId) throws IOException {
        String url = "https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId;
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Task query failed: " + response.code() + " - " + errorBody);
            }
            return response.body().string();
        }
    }

    private String parseTaskId(String response) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode output = jsonNode.get("output");
        return output.get("task_id").asText();
    }

    private String parseTaskStatus(String response) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode output = jsonNode.get("output");
        return output.get("task_status").asText();
    }



}