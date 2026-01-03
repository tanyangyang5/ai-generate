package com.example.aigenerate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "场景空镜资产（用于生成不含人物的背景图）")
public class SceneAsset {

    @Schema(description = "【场景名称】格式：地点 - 时间 - 天气",
            example = "古堡大厅 - 雾夜")
    private String sceneName;

    @Schema(description = "【风格渲染词】英文提示词，用于AI绘图模型",
            example = "Dark Fantasy, Gothic Architecture, Volumetric Fog, Octane Render, 8k texture")
    private String styleRenderKeywords;

    @Schema(description = "【光影/色调】符合风格的配色与布光方案",
            example = "血红色与深黑色高对比，地面红雾发光，远处微弱月光")
    private String lightingTone;

    @Schema(description = "【环境/陈设】空间结构与固定陈设（不含可移动道具）",
            example = "高耸肋拱天花板，斑驳石材墙，蒙白布雕像，积尘水晶吊灯")
    private String environmentProps;
}