package com.example.aigenerate.image.service;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisOutput;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.example.aigenerate.image.dto.ImageGenerationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageGenerationService {

    @Value("${api.key}")
    private String apiKey;

    @Value("${region}")
    private String region;

    public String generateImage(ImageGenerationRequest request) {
        // 设置区域URL (北京/新加坡)
        String baseUrl = "https://dashscope.aliyuncs.com/api/v1";
        if ("singapore".equalsIgnoreCase(region)) {
            baseUrl = "https://dashscope-intl.aliyuncs.com/api/v1";
        }
        Constants.baseHttpApiUrl = baseUrl;

        // 配置参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true);  // 自动扩展提示词
        parameters.put("watermark", false);     // 无水印

        // 构建请求参数
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(apiKey)
                .model(request.getModel())
                .prompt(request.getPrompt())
                .n(1)
                .size(request.getSize())
                .parameters(parameters)
                .build();

        // 调用模型
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        try {
            ImageSynthesisResult result = imageSynthesis.call(param);

            ImageSynthesisOutput output = result.getOutput();
            List<Map<String, String>> results = output.getResults();

            if (results == null || results.isEmpty()) {
                throw new RuntimeException("No image results returned from API");
            }
            // 返回第一个结果的URL
            return results.getFirst().get("url");
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException("生成图片失败: " + e.getMessage(), e);
        }
    }


}
