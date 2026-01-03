// 各子模块（简化，仅保留文本描述，图像由 Service 动态生成）
package com.example.aigenerate.response.textToImage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StoryboardModule {
    private String rawText; // 原始分镜文本（含▲和对话）
}
