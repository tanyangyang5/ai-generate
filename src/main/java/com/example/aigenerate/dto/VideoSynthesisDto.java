package com.example.aigenerate.dto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
public class VideoSynthesisDto {

    // === 请求体（保持不变）===
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Request {
        private String model = "wan2.5-i2v-preview";
        private Input input;
        private Parameters parameters;

        @Data
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Input {
            private String prompt;
            private String imgUrl; // → img_url
            private String firstFrameUrl;
            private String lastFrameUrl;
        }

        @Data
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Parameters {
            private String resolution = "480P";
            private Boolean promptExtend = true;
            private Integer duration = 10;
            private Boolean audio = true;
        }
    }

    // === 响应体（按你给的 JSON 重构）===
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Response {
        private String requestId; // → request_id
        private Output output;
        private Usage usage;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Output {
        private String taskId;
        private String taskStatus;
        private String submitTime;      // "2025-09-25 11:07:28.590"
        private String scheduledTime;   // "2025-09-25 11:07:35.349"
        private String endTime;         // "2025-09-25 11:17:11.650"
        private String origPrompt;
        private String videoUrl;        // 注意：直接在这里，不是 result.url！
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Usage {
        private Integer duration;
        private Integer inputVideoDuration;
        private Integer outputVideoDuration;
        private Integer videoCount;
        private Integer sr; // SR = Super Resolution? 720
    }
}