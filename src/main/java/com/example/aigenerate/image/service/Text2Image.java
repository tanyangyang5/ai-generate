package com.example.aigenerate.service;// Copyright (c) Alibaba, Inc. and its affiliates.

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import java.util.HashMap;
import java.util.Map;

public class Text2Image {
    static {
        // 以下为北京地域url，若使用新加坡地域的模型，需将url替换为：https://dashscope-intl.aliyuncs.com/api/v1
        Constants.baseHttpApiUrl = "https://dashscope.aliyuncs.com/api/v1";
    }

    // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
    // 若没有配置环境变量，请用百炼API Key将下行替换为：static String apiKey = "sk-xxx"
    static String apiKey = "sk-f1b7304ab25248ed937df6c2a64c857d";

    public static void basicCall() throws ApiException, NoApiKeyException {
        String prompt = "大明远征蒙古的壮阔场面";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true);
        parameters.put("watermark", false);
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(apiKey)
                        .model("wan2.2-t2i-flash")
                        .prompt(prompt)
                        .n(1)
                        .size("1328*1328")
                        .parameters(parameters)
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---同步调用，请等待任务执行----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
    }

    public static void main(String[] args){
        try{
            basicCall();
        }catch(ApiException|NoApiKeyException e){
            System.out.println(e.getMessage());
        }
    }
}