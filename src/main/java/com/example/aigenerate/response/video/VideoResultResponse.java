package com.example.aigenerate.response.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class VideoResultResponse {

    /**
     * 任务 ID（等同于 video_id）
     */
    @JsonProperty("task_id")
    private String taskId;

    /**
     * 视频下载 URL（主字段，直接可用）
     */
    @JsonProperty("video_result")
    private String videoResult;

    /**
     * 视频结果列表（通常只有一个元素）
     */
    @JsonProperty("video_results")
    private List<VideoUrl> videoResults;

    /**
     * 视频唯一 ID
     */
    @JsonProperty("video_id")
    private String videoId;

    /**
     * 任务状态：succeed / failed / processing 等
     */
    @JsonProperty("task_status")
    private String taskStatus;

    /**
     * 附加消息（如 "Video generation completed and uploaded to R2"）
     */
    private String message;

    /**
     * 视频时长（字符串形式，如 "8"）
     */
    private String duration;

    // 内部类：表示单个视频 URL 条目
    @Data
    public static class VideoUrl {
        private String url;
    }
}