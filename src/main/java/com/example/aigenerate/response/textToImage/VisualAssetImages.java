package com.example.aigenerate.response.textToImage;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "一个场景的视觉资产图像结果（Base64）")
public class VisualAssetImages {

    private String storyboardPrompt;

    @Schema(description = "角色三视图图像（Base64 Data URL）")
    @ArraySchema(arraySchema = @Schema(description = "通常返回1张"))
    private List<String> characterImages;
    private String characterPrompt;

    @Schema(description = "场景空镜图像")
    private List<String> sceneImages;
    private String scenePrompt;

    @Schema(description = "道具特写图像")
    private List<String> propImages;
    private String propPrompt;
}