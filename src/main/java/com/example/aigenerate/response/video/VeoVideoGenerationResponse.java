package com.example.aigenerate.response.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VeoVideoGenerationResponse {
    /**
     * 任务 ID，用于后续轮询结果
     */
    @JsonProperty("task_id")
    private String taskId;

    /**
     * 任务状态：
     * - "succeed"：表示请求已成功提交（非视频生成完成！）
     * - 可能还有 "failed", "processing" 等（但首次提交通常为 succeed）
     */
    @JsonProperty("task_status")
    private String taskStatus;

    /**
     * 人类可读消息
     */
    private String message;
}