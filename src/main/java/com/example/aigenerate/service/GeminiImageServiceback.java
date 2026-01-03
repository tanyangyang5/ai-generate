// GeminiImageService.java
package com.example.aigenerate.service;

import com.example.aigenerate.response.GeminiImageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiImageServiceback {
    // 使用 OkHttp 客户端（可复用，线程安全）
    private final OkHttpClient okHttpClient ;
    private final ObjectMapper objectMapper ;
    @Value("${ezlinkai.api.key}")
    private String apiKey;
    @Value("${ezlinkai.api.geminiImageUrl}")
    private String imageUrl;

    public List<String> textGenerateImages(String prompt){
        // 构造请求体
        String jsonRequest = """
        {
          "contents": [{
            "parts": [{ "text": "%s" }]
          }]
        }
        """.formatted(prompt);

        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(imageUrl)
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                log.info(response.body().string());
                log.info(response.message());
                throw new RuntimeException("Unexpected code " + response);
            }

            // 解析JSON响应
            assert response.body() != null;
            GeminiImageResponse geminiImageResponse = objectMapper.readValue(response.body().string(), GeminiImageResponse.class);
            return extractBase64Images(geminiImageResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> extractBase64Images(GeminiImageResponse apiResponse) {
        return apiResponse.getCandidates().stream()
                .flatMap(candidate -> candidate.getContent().getParts().stream())
                .filter(part -> part.getInlineData() != null && part.getInlineData().getData() != null)
                .map(part -> "data:" + part.getInlineData().getMimeType() + ";base64," + part.getInlineData().getData())
                .collect(Collectors.toList());
    }


    public List<String> editImage(
            String prompt,
            List<String> base64Images,
            String aspectRatio,
            String imageSize)  {

        if (base64Images == null || base64Images.isEmpty()) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        // 构建 parts：第一个是 text，后面全是 inline_data
        StringBuilder partsBuilder = new StringBuilder();
        partsBuilder.append("{\"text\": \"").append(prompt.replace("\"", "\\\"")).append("\"}");

        for (String img : base64Images) {
            // 转义 Base64 中可能存在的特殊字符（虽然 Base64 通常安全，但保险起见）
            String safeBase64 = img.replace("\\", "\\\\").replace("\"", "\\\"");
            partsBuilder.append(",{\"inline_data\":{\"mime_type\":\"image/png\",\"data\":\"")
                    .append(safeBase64)
                    .append("\"}}");
        }

        String jsonBody = """
        {
          "contents": [{
            "parts": [%s]
          }],
          "generationConfig": {
            "responseModalities": ["TEXT", "IMAGE"],
            "imageConfig": {
              "aspectRatio": "%s",
              "imageSize": "%s"
            }
          }
        }
        """.formatted(partsBuilder.toString(), aspectRatio, imageSize);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(imageUrl)
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new RuntimeException("API Error: " + response.code() + " - " + errorBody);
            }
            String responseBody = response.body().string();
            GeminiImageResponse apiResponse = objectMapper.readValue(responseBody, GeminiImageResponse.class);
            return extractBase64Images(apiResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}