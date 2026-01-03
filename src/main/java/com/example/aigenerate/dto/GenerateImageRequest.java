package com.example.aigenerate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 图片生成请求实体
 * <p>
 * 用于向 Gemini 模型发送多图 + 文本提示，生成合成图像。
 * </p>
 */
@Data
public class GenerateImageRequest {

    @Schema(
            description = "文本提示（Prompt），描述你希望生成的图像内容,不要包含\n\r这样的换行符号 tab 符号",
            example = "角色全名:老年辛弃疾（画外音使用中文）,【风格化造型】枯瘦儒将气，墨线勾骨、淡染衰白,【面部/特征】深刻皱纹，唇裂，眼窝阴影浓，气息沉重,【服饰/材质】旧青灰长衫，粗麻与旧棉质感，边角墨染般磨损",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Prompt 不能为空或仅包含空白字符")
    private String prompt;

    @Schema(
            description = "输入图片的 Base64 编码列表（不包含 data:image/... 前缀），支持 1 张或多张",
            example = "[\"iVBORw0KGgoAAAANSUhEUgAA...\", \"iVBORw0KGgoAAAANSUhEUgAB...\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    //@NotEmpty(message = "至少需要提供一张 Base64 图片")
    private List<String> base64Images;

    @Schema(
            description = "图像生成配置（尺寸、比例等）,图片编辑时传入",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    //@NotNull(message = "imageConfig 不能为空")
    private ImageConfig imageConfig;

    @Data
    public static class ImageConfig {

        @Schema(
                description = "输出图像的宽高比，例如：5:4、16:9、1:1",
                example = "5:4"
        )
        private String aspectRatio;

        @Schema(
                description = "输出图像的尺寸规格，例如：2K、1024x1024、4K",
                example = "2K"
        )
        private String imageSize;
    }

    @Schema(
            description = "目标风格",
            example = "国风水墨",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userStyle;
}