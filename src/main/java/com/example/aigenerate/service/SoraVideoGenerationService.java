package com.example.aigenerate.service;

import com.example.aigenerate.dto.SoraVideoGenerationRequest;
import com.example.aigenerate.response.video.SoraVideoGenerationResponse;
import com.example.aigenerate.response.video.VideoResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoraVideoGenerationService {

    @Value("${ezlinkai.api.soraVideoUrl}")
    private String apiUrl;

    @Value("${ezlinkai.api.key}")
    private String apiKey;

    // 目标尺寸（可配置）1280x720
    private static final int TARGET_WIDTH = 1280;
    private static final int TARGET_HEIGHT = 720;

    //    private final OkHttpClient httpClient = new OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .readTimeout(120, TimeUnit.SECONDS)
//            .build();
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
    // 使用 OkHttp 客户端（可复用，线程安全）
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public SoraVideoGenerationResponse generateVideo(SoraVideoGenerationRequest request) {
        try {
            MultipartBody.Builder multipartBuilder = null;
            try {
                // 如果有参考图，先压缩
                byte[] compressedImageBytes = null;
                String originalFilename = "reference.jpg";

                if (request.getInputReference() != null && !request.getInputReference().isEmpty()) {
                    originalFilename = request.getInputReference().getOriginalFilename();
                    if (originalFilename == null || originalFilename.isEmpty()) {
                        originalFilename = "reference.jpg";
                    }

                    byte[] originalBytes = request.getInputReference().getBytes();
                    //compressedImageBytes = resizeImageToBytes(originalBytes, TARGET_WIDTH, TARGET_HEIGHT);
                    //resizeAndCropToExactSize
                    compressedImageBytes = resizeAndCropToExactSize(originalBytes, TARGET_WIDTH, TARGET_HEIGHT);
                }

                // 构建 multipart 请求体
                multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("model", request.getModel())
                        .addFormDataPart("prompt", request.getPrompt())
                        .addFormDataPart("seconds", String.valueOf(request.getSeconds()))
                        .addFormDataPart("size", request.getSize());

                // 添加压缩后的图片（如果存在）
                if (compressedImageBytes != null) {
                    MediaType mediaType = getMediaType(originalFilename);
                    RequestBody imageBody = RequestBody.create(compressedImageBytes, mediaType);
                    multipartBuilder.addFormDataPart("input_reference", originalFilename, imageBody);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RequestBody requestBody = multipartBuilder.build();

            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = okHttpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    assert response.body() != null;
                    log.error(response.body().string());
                    log.error("EzLinkAI API error: {} - {}", response.code(), response.message());
                    throw new RuntimeException("API returned error: " + response.code());
                }

                ResponseBody body = response.body();
                if (body == null) {
                    throw new RuntimeException("Empty response from API");
                }

                String json = body.string();
                log.info("sora video response: {}", json);
                return objectMapper.readValue(json, SoraVideoGenerationResponse.class);
            }

        } catch (IOException e) {
            log.error("I/O error during video generation", e);
            throw new RuntimeException("Failed to process image or call API", e);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw new RuntimeException("Video generation failed", e);
        }
    }

    /**
     * 将原始图片字节数组压缩（缩放）为指定宽高，并返回新的字节数组
     */
    private byte[] resizeImageToBytes(byte[] imageBytes, int width, int height) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(new ByteArrayInputStream(imageBytes))
                    .size(width, height)
                    .keepAspectRatio(true)   // 保持宽高比，避免拉伸
                    .outputQuality(0.9f)     // JPEG 质量（0.0 ~ 1.0）
                    .outputFormat("JPEG")    // 统一转为 JPEG（减小体积）
                    .toOutputStream(baos);

            return baos.toByteArray();
        }
    }

    /**
     * 将图片缩放并居中裁剪为 exactly width x height，无变形
     */
    private byte[] resizeAndCropToExactSize(byte[] imageBytes, int targetWidth, int targetHeight) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(new ByteArrayInputStream(imageBytes))
                    .size(targetWidth, targetHeight)      // 先缩放到覆盖目标区域（保持比例）
                    .crop(Positions.CENTER)               // 居中裁剪到 exact 尺寸
                    .outputQuality(0.9f)
                    .outputFormat("JPEG")
                    .toOutputStream(baos);
            return baos.toByteArray();
        }
    }


    private MediaType getMediaType(String filename) {
        String ext = filename.toLowerCase();
        if (ext.endsWith(".png")) {
            return MediaType.get("image/png");
        } else if (ext.endsWith(".jpg") || ext.endsWith(".jpeg")) {
            return MediaType.get("image/jpeg");
        } else if (ext.endsWith(".webp")) {
            return MediaType.get("image/webp");
        } else {
            return MediaType.get("image/jpeg"); // 默认 JPEG
        }
    }


    public VideoResultResponse getVideoResult(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }

        HttpUrl url = HttpUrl.parse(apiUrl.replace("/v1/videos", "/v1/video/generations/result"))
                .newBuilder()
                .addQueryParameter("taskid", taskId)
                .addQueryParameter("response_format", "url")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to fetch video result: {} - {}", response.code(), response.message());
                throw new RuntimeException("API error: " + response.code() + " " + response.message());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new RuntimeException("Empty response from API");
            }

            String json = body.string();
            return objectMapper.readValue(json, VideoResultResponse.class);

        } catch (IOException e) {
            log.error("Network or parsing error when fetching video result", e);
            throw new RuntimeException("Failed to retrieve video generation result", e);
        }

    }
}