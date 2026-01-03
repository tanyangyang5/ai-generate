package com.example.aigenerate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "核心角色视觉资产卡（用于Midjourney/Stable Diffusion角色一致性生成）")
public class CharacterAsset {

    @Schema(description = "角色全名", example = "范海辛")
    private String roleName;

    @Schema(description = "【风格化造型】结合全局风格的整体形象描述",
            example = "维多利亚时期哥特风格，颓废而硬朗，带有浓重的油画质感")
    private String stylizedLook;

    @Schema(description = "【面部/特征】五官、年龄感、皮肤质感等细节",
            example = "面色苍白，眼窝深陷，胡茬凌乱，眼神疲惫而冷酷")
    private String facialFeatures;

    @Schema(description = "【服饰/材质】具体衣着款式与面料材质",
            example = "磨损的黑色长皮风衣（皮革纹理清晰），高耸立领，沾泥皮靴")
    private String costumeMaterial;
}