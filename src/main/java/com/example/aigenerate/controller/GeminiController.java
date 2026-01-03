package com.example.aigenerate.controller;

import com.example.aigenerate.dto.GenerateImageRequest;
import com.example.aigenerate.response.textToImage.VisualAssetImages;
import com.example.aigenerate.service.GeminiImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiImageService imageService;

    @PostMapping("/generate-images")
    @Operation(summary = "文生图")
    public ResponseEntity<List<String>> generateImages(@RequestBody GenerateImageRequest req) {
        return ResponseEntity.ok(imageService.textGenerateImages(req.getUserStyle(),req.getPrompt()));
    }

    @PostMapping("/edit-image")
    @Operation(summary = "图片编辑")
    public List<String> generate(@RequestBody GenerateImageRequest request) {
        return imageService.editImage(
                request.getPrompt(),
                request.getBase64Images(),
                request.getImageConfig().getAspectRatio(),
                request.getImageConfig().getImageSize()
        );
    }

}