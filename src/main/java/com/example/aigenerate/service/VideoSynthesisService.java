package com.example.aigenerate.service;


import com.alibaba.dashscope.utils.StringUtils;
import com.example.aigenerate.dto.VideoSynthesisDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
@Slf4j
public class VideoSynthesisService {

    @Value("${aliyun.dashscope.video-synthesis-url}")
    private String apiUrl ;

    @Value("${aliyun.dashscope.image-video-url}")
    private String image2videoUrl ;

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
        String url = apiUrl;
        if(!ObjectUtils.isEmpty(request.getInput().getFirstFrameUrl()) && !ObjectUtils.isEmpty(request.getInput().getLastFrameUrl())){
            url = image2videoUrl;
        }
        log.info(url);
        Request httpRequest = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("X-DashScope-Async", "enable")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                throw new IOException("Submit failed: " + response.code() + " - " + response.body().string());
            }
            assert response.body() != null;
            return objectMapper.readValue(response.body().string(), VideoSynthesisDto.Response.class);
        }
    }


}