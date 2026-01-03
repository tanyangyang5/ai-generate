package com.example.aigenerate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "一个完整影视场景的四大标准化视觉资产（按场景拆分）")
public class VisualAssetScene {

    @Schema(description = "模块一：风格化分镜脚本")
    private StoryboardTimeline storyboard;

    @Schema(description = "模块二：人物建模资产（仅当前场景核心角色）")
    private CharacterAsset character;

    @Schema(description = "模块三：场景空镜资产（背景底图）")
    private SceneAsset sceneAsset;

    @Schema(description = "模块四：关键道具资产（如有）")
    private PropAsset prop;
}