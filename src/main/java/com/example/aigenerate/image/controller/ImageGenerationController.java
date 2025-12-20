package com.example.aigenerate.image.controller;

import com.example.aigenerate.image.dto.ImageGenerationRequest;
import com.example.aigenerate.image.service.ImageGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image")
public class ImageGenerationController {

    private final ImageGenerationService imageService;

    @Autowired
    public ImageGenerationController(ImageGenerationService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/generate")
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
