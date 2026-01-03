package com.example.aigenerate.dto;

import lombok.Data;
import java.util.List;

@Data
public class VeoGenerationRequest {

    private String model = "veo-3.0-generate-preview"; // 固定模型
    private List<VeoInstance> instances;
    private VeoParameters parameters;

    @Data
    public static class VeoInstance {
        private String prompt;
        private VeoImage image; // 可选
    }

    @Data
    public static class VeoImage {
        private String bytesBase64Encoded; // Base64 编码的图片字节
        private String mimeType;           // 如 "image/jpeg"
    }

    @Data
    public static class VeoParameters {
        private String aspectRatio = "16:9";      // "16:9", "9:16", "1:1"
        private Integer durationSeconds = 8;      // 5~10 秒（Veo 限制）
        private Boolean enhancePrompt = true;
        private Boolean generateAudio = false;
        private String negativePrompt;
        private String personGeneration = "allow"; // "allow", "block"
        private Integer sampleCount = 1;
        private Long seed;
    }
}