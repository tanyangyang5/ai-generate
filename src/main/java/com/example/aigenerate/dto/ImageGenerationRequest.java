package com.example.aigenerate.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ImageGenerationRequest {

    // 手动添加getter方法（关键！）
    @Schema(description = "AI 模型名称", example = "wan2.5-i2v-preview", requiredMode = Schema.RequiredMode.REQUIRED)
    private String model;
    @Schema(description = "输入prompt", example = "", requiredMode = Schema.RequiredMode.REQUIRED)// 模型名称 (wan2.2-t2i-flash)
    private String prompt;
    @Schema(description = "输入prompt", example = "", requiredMode = Schema.RequiredMode.NOT_REQUIRED)// 模型名称 (wan2.2-t2i-flash)// 提示词
    private String size = "1328*1328";        // 图片尺寸 (默认: 1328*1328)

//    public ImageGenerationRequest() {
//        this.size = "1328*1328";
//    }

//    // 手动添加getter方法（关键！）
//    public String getModel() { return model; }
//    public String getPrompt() { return prompt; }
//    public String getSize() { return size; }
//
//
//    // 可选：添加setter
//    public void setModel(String model) { this.model = model; }
//    public void setPrompt(String prompt) { this.prompt = prompt; }
//    public void setSize(String size) { this.size = size; }


}
