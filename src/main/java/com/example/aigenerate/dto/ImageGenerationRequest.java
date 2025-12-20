package com.example.aigenerate.dto;



public class ImageGenerationRequest {

    // 手动添加getter方法（关键！）
    private String model;       // 模型名称 (wan2.2-t2i-flash)
    private String prompt;     // 提示词
    private String size;        // 图片尺寸 (默认: 1328*1328)

    public ImageGenerationRequest() {
        this.size = "1328*1328";
    }

    // 手动添加getter方法（关键！）
    public String getModel() { return model; }
    public String getPrompt() { return prompt; }
    public String getSize() { return size; }


    // 可选：添加setter
    public void setModel(String model) { this.model = model; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public void setSize(String size) { this.size = size; }


}
