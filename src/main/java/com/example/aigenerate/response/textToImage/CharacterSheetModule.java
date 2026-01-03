package com.example.aigenerate.response.textToImage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CharacterSheetModule {
    @Schema(example = "辛弃疾（暮年）")
    private String roleName;

    @Schema(example = "Character sheet, three views (front, side, back), full body, neutral pose, white background.")
    private String compositionView;

    @Schema(example = "Traditional Chinese Ink Wash Painting style...")
    private String stylizedLook;

    @Schema(example = "白发苍苍，胡须及胸，皱纹深刻...")
    private String appearance;

    @Schema(example = "灰白棉麻长袍，墨点纹理，黑色布带...")
    private String costumeGear;
}