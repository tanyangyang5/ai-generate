package com.example.aigenerate.controller;

import com.example.aigenerate.dto.SoraVideoGenerationRequest;
import com.example.aigenerate.response.video.SoraVideoGenerationResponse;
import com.example.aigenerate.response.video.VideoResultResponse;
import com.example.aigenerate.service.SoraVideoGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoGenerationController {

    private final SoraVideoGenerationService videoGenerationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SoraVideoGenerationResponse> generateVideo(
            @RequestParam String model,
            @RequestParam String prompt,
            @RequestParam(required = false) MultipartFile inputReference,
            @RequestParam(defaultValue = "8") Integer seconds,
            @RequestParam(defaultValue = "1280x720") String size) {

        SoraVideoGenerationRequest request = new SoraVideoGenerationRequest();
        request.setModel(model);
        request.setPrompt(prompt);
        request.setInputReference(inputReference);
        request.setSeconds(seconds);
        request.setSize(size);

        SoraVideoGenerationResponse response = videoGenerationService.generateVideo(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询视频生成任务状态和结果
     * GET /api/videos/result?taskId=xxx
     */
    @GetMapping("/result")
    public ResponseEntity<VideoResultResponse> getVideoResult(@RequestParam String taskId) {
        VideoResultResponse result = videoGenerationService.getVideoResult(taskId);
        return ResponseEntity.ok(result);
    }
}