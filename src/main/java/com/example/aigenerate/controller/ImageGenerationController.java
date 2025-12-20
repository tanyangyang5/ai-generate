package com.example.aigenerate.controller;

import com.example.aigenerate.dto.ImageGenerationRequest;
import com.example.aigenerate.service.ImageGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image")
@Tag(name = "图片生成", description = "AI 图片生成接口")
public class ImageGenerationController {

    private final ImageGenerationService imageService;

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/generate")
    @Operation(summary = "提交图片生成任务")
    public ResponseEntity<String> generateImage(@RequestBody ImageGenerationRequest request) {
        try {
            String imageUrl = imageService.generateImage(request);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("生成失败: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Image Generation API is working!";
    }


}
