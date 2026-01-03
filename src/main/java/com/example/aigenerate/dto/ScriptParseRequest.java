package com.example.aigenerate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScriptParseRequest {
    @Schema(description = "原始剧本或小说片段",
            example = "猎魔人范海辛推开古堡沉重的大门...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "剧本不能为空")
    private String script;
    @Schema(description = "模型名称",
            example = "gpt-5.2", requiredMode = Schema.RequiredMode.REQUIRED)
    private String model = "gpt-5.2";

    @Schema(description = "目标艺术风格，如 '水墨国风'、'赛博朋克'、'暗黑哥特'",
            example = "暗黑哥特", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userStyle;


}
