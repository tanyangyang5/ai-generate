package com.example.aigenerate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SoraVideoGenerationRequest {

    @NotBlank(message = "Model is required")
    private String model = "sora-2";

    @NotBlank(message = "Prompt is required")
    private String prompt;

    private MultipartFile inputReference; // 可选

    @Min(value = 1, message = "Seconds must be at least 1")
    @Max(value = 30, message = "Seconds cannot exceed 30")
    private Integer seconds = 8;

    @Pattern(regexp = "^\\d+x\\d+$", message = "Size must be in format 'widthxheight', e.g., '1280x720'")
    private String size = "1280x720";
}