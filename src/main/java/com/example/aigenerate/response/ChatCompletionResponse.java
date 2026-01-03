package com.example.aigenerate.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Integer index;
        private Message message;
        private String finish_reason;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String role;
        private String content;
        private String refusal; // 👈 新增字段，用于处理拒绝响应
        // 👇 新增 annotations 字段
        private List<Annotation> annotations;
    }

    // 👇 定义 Annotation 结构（简化版，按需扩展）
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Annotation {
        private String type; // e.g., "file_path"
        private String text; // 原始文本，如 "/mnt/data/xxx"
        private FilePath filePath; // 如果是文件路径类型

        @Data
        public static class FilePath {
            private String file_id;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}