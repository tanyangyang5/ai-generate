package com.example.aigenerate.controller;

import com.example.aigenerate.dto.VeoGenerationRequest;
import com.example.aigenerate.response.video.VeoVideoGenerationResponse;
import com.example.aigenerate.service.VeoVideoGenerationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veo")
@RequiredArgsConstructor
public class VeoVideoController {

    private final VeoVideoGenerationService veoService;

    /**
     * 生成 Veo 视频（支持文生视频 / 图生视频）
     * POST /api/veo/videos
     */
    @PostMapping("/videos")
    public ResponseEntity<VeoVideoGenerationResponse> generateVeoVideo(
            @RequestBody VeoGenerationRequest request) {
        VeoVideoGenerationResponse response = veoService.generateVideo(request);
        return ResponseEntity.ok(response);
    }
}