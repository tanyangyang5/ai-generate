package com.example.aigenerate.image.dto;


import lombok.Data;

@Data
public class ImageGenerationRequest {

    private String model;       // 模型名称 (wan2.2-t2i-flash)
    private String prompt;      // 提示词
    private String size;        // 图片尺寸 (默认: 1328*1328)

    public ImageGenerationRequest() {
        this.size = "1328*1328";
    }


}
