package com.example.aigenerate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScriptParseRequest {
    @NotBlank(message = "剧本不能为空")
    private String script;

    private String model;
}
