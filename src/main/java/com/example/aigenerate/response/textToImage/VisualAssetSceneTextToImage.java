// com.example.aigenerate.asset.VisualAssetScene.java
package com.example.aigenerate.response.textToImage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "单个场景的完整视觉资产包")
public class VisualAssetSceneTextToImage {

    @Schema(description = "模块一：风格化分镜脚本（文本）")
    private StoryboardModule storyboard;

    @Schema(description = "模块二：角色三视图资产（文本描述 + 可选图像）")
    private CharacterSheetModule character;

    @Schema(description = "模块三：空镜场景资产")
    private SceneAssetModule scene;

    @Schema(description = "模块四：关键道具资产")
    private PropAssetModule prop;
}