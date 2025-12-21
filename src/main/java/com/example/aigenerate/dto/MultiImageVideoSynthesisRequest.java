package com.example.aigenerate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class MultiImageVideoSynthesisRequest {

    @Schema(
            description = "视频生成提示词，描述视频内容的自然语言描述",
            example = "视频中，一位女孩自晨雾缭绕的古老森林深处款款走出，她步伐轻盈，镜头捕捉她每一个灵动瞬间",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String prompt;

    @Schema(
            description = "参考图片URL列表，最多3张，用于提供主体或背景参考",
            example = "[\"http://wanx.alicdn.com/material/20250318/image_reference_2_5_16.png\", \"http://wanx.alicdn.com/material/20250318/image_reference_1_5_16.png\"]",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 3
    )
    private List<String> refImagesUrl;

    @Schema(
            description = "参考图片用途标识数组，与refImagesUrl一一对应，可选值：obj(主体), bg(背景)。最多允许1个bg",
            example = "[\"obj\", \"bg\"]",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            minLength = 1,
            maxLength = 3
    )
    private List<String> objOrBg;


}