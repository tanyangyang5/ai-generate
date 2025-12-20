package com.example.aigenerate.service;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisOutput;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.example.aigenerate.dto.ImageGenerationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ImageGenerationService {

    @Value("${aliyun.apiKey}")
    private String apiKey;

    @Value("${region}")
    private String region;

    @Value("${aliyun.dashscope.image-synthesis-url}")
    private String imageSynthesisUrl;

    public String generateImage(ImageGenerationRequest request) {

        if ("singapore".equalsIgnoreCase(region)) {
            imageSynthesisUrl = "https://dashscope-intl.aliyuncs.com/api/v1";
        }
        Constants.baseHttpApiUrl = imageSynthesisUrl;

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
            log.info( "1111" );
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
