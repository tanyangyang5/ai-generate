package com.example.aigenerate.response.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SoraVideoGenerationResponse {

    /**
     * 视频任务唯一 ID
     */
    private String id;

    /**
     * 对象类型，固定为 "video"
     */
    private String object;

    /**
     * 创建时间戳（Unix 秒）
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * 当前状态：queued / processing / completed / failed
     */
    private String status;

    /**
     * 完成时间戳（Unix 秒），未完成时为 null
     */
    @JsonProperty("completed_at")
    private Long completedAt;

    /**
     * 错误信息（如果失败）
     */
    private String error;

    /**
     * 过期时间戳（Unix 秒），null 表示永不过期或未设置
     */
    @JsonProperty("expires_at")
    private Long expiresAt;

    /**
     * 使用的模型名称
     */
    private String model;

    /**
     * 生成进度百分比（0~100），部分 API 可能返回小数或 null
     */
    private Integer progress;

    /**
     * 生成提示词（prompt）
     */
    private String prompt;

    /**
     * 如果是 remix 视频，此字段为源视频 ID
     */
    @JsonProperty("remixed_from_video_id")
    private String remixedFromVideoId;

    /**
     * 视频时长（字符串形式，如 "8"）
     */
    private String seconds;

    /**
     * 分辨率，格式如 "1280x720"
     */
    private String size;
}