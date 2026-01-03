package com.example.aigenerate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关键道具高清资产（用于物品一致性生成）")
public class PropAsset {

    @Schema(description = "【道具名称】", example = "驱魔左轮")
    private String propName;

    @Schema(description = "【类别】如兵器、魔法物品、电子产品等", example = "魔法火器")
    private String category;

    @Schema(description = "【视觉详情】形状、磨损、发光等细节",
            example = "纯银枪身刻满拉丁文符文，黑檀木握把嵌十字徽章")
    private String visualDetails;

    @Schema(description = "【风格化质感】在目标风格下的物理表现",
            example = "金属氧化做旧，符文在红雾中折射神圣蓝光")
    private String stylizedTexture;
}