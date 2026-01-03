package com.example.aigenerate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class Scene {
    @Schema(
            description = "场景标题"
    )
    private String sceneTitle; // 场景标题
    @Schema(
            description = "场景标题"
    )
    private String sceneDescription; // 场景描述
    @Schema(
            description = "角色对话列表"
    )
    private List<String> roleDialogues; // 角色对话列表
    @Schema(
            description = "动作指示列表"
    )
    private List<String> actionInstructions; // 动作指示列表
    @Schema(
            description = "出场人物"
    )
    private String appearingCharacters;      // 出场人物（字符串，如“辛弃疾、张安国”）
    @Schema(
            description = "道具"
    )
    private String keyProps;//道具
}