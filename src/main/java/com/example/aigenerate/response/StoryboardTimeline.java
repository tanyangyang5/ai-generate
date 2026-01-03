package com.example.aigenerate.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "风格化分镜脚本（用于Sora/Runway等视频生成）")
public class StoryboardTimeline {

    @ArraySchema(
            arraySchema = @Schema(description = "所有以 ▲ 开头的镜头描述行，每行一个镜头"),
            schema = @Schema(example = "▲低角度广角镜头，仰拍古堡大门缓缓打开...")
    )
    private List<String> shotLines;

    @ArraySchema(
            arraySchema = @Schema(description = "角色对话行，格式：角色名（情绪）：台词"),
            schema = @Schema(example = "范海辛（冷峻）：这雾……不对劲。")
    )
    private List<String> dialogues;
}