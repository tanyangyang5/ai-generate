package com.example.aigenerate.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
public class VideoSynthesisDto {

    // === 请求体（保持不变）===
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Request {
        @Schema(description = "AI 模型名称", example = "wan2.5-i2v-preview", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
        private String model;
        @Schema(description = "输入参数", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
        private Input input;
        @Schema(description = "视频生成设置", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
        private Parameters parameters;

        @Data
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Input {
            @Schema(description = "prompt", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
            private String prompt;
            @Schema(description = "imgUrl 根据首帧生成时用这个参数", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
            private String imgUrl;
            @Schema(description = "firstFrameUrl 根据首尾帧生成时用这个参数", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
            private String firstFrameUrl;
            @Schema(description = "lastFrameUrl 根据首尾帧生成时用这个参数", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
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