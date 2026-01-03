package com.example.aigenerate.response;

import lombok.Data;
import java.util.List;

@Data
public class GeminiImageResponse {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
    }

    @Data
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    public static class Part {
        private String text;
        private String thoughtSignature;
        private InlineData inlineData; // 注意：text 和 inlineData 不会同时存在
    }

    @Data
    public static class InlineData {
        private String mimeType; // e.g., "image/jpeg"
        private String data;     // Base64 string
    }
}