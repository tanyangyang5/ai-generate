package com.example.aigenerate.service;

import com.example.aigenerate.config.AiApiConfig;
import com.example.aigenerate.dto.ChatCompletionRequest;
import com.example.aigenerate.response.ChatCompletionResponse;
import com.example.aigenerate.response.Scene;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Service
public class AiScriptParsingService {

    @Autowired
    private AiApiConfig aiApiConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Scene> parseScriptToStructuredParagraphs(String scriptContent) {
        String systemPrompt = """
            你是一位专业的剧本结构化解析器，请严格按以下规则处理用户提供的剧本内容：
            
            1. **按场景切分**：每当时间、地点或主视角发生明显变化时，视为新场景。
            2. **每个场景必须包含以下四个部分（按顺序）**：
               - **【场景标题】**：格式为“场景 N - [室内/室外] / [时间] / [地点]”，例如：“场景 1 - 室内 / 白天 / 咖啡馆”
               - **【场景描述】**：仅包含原文中的环境、氛围、背景等叙述性文字（非对话）。若无，则写“无”。
               - **【角色对话】**：每行格式为“角色名：台词”。保留原始台词内容和标点，不要润色。若无对话，写“无”。
               - **【动作指示】**：提取所有括号 [] 或舞台说明类文字（如 [走向门口]、(苦笑) 等）。若无，写“无”。
            
            3. **输出格式要求**：
               - 使用中文标点
               - 每个场景以“---”分隔
               - 不要添加任何解释、总结、前缀或后缀
               - 不要修改、删减或扩写原始内容
               - 若原文未标明场景信息，请根据上下文合理推断标题（如“场景 1 - 室内 / 未知 / 办公室”）
            
            4. **禁止行为**：
               - 不要生成虚构内容
               - 不要合并多个场景
               - 不要使用 JSON、XML 或其他结构化数据格式
            
            请严格按照上述规则输出。
            """;

        List<ChatCompletionRequest.Message> messages = Arrays.asList(
                new ChatCompletionRequest.Message("system", systemPrompt),
                new ChatCompletionRequest.Message("user", scriptContent)
        );

        ChatCompletionRequest request = new ChatCompletionRequest(aiApiConfig.getModel(), messages);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + aiApiConfig.getKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String jsonBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            var response = restTemplate.postForEntity(
                    aiApiConfig.getUrl(),
                    entity,
                    ChatCompletionResponse.class
            );

            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                String structuredText = response.getBody().getChoices().get(0).getMessage().getContent();
                return parseStructuredText(structuredText);
            } else {
                throw new RuntimeException("AI 返回结果为空");
            }

        } catch (Exception e) {
            throw new RuntimeException("调用 AI 接口失败: " + e.getMessage(), e);
        }
    }

    private List<Scene> parseStructuredText(String structuredText) {
        List<Scene> scenes = new ArrayList<>();
        String[] parts = structuredText.split("---");

        Pattern titlePattern = Pattern.compile("【场景标题】\\s*场景 \\d+ - (.*)");
        Pattern descriptionPattern = Pattern.compile("【场景描述】\\s*(.*)");
        Pattern dialoguesPattern = Pattern.compile("【角色对话】\\s*((?:.*?：.*?(?:\n|$))+)");
        Pattern actionsPattern = Pattern.compile("【动作指示】\\s*(.*)");

        for (String part : parts) {
            Matcher titleMatcher = titlePattern.matcher(part);
            Matcher descriptionMatcher = descriptionPattern.matcher(part);
            Matcher dialoguesMatcher = dialoguesPattern.matcher(part);
            Matcher actionsMatcher = actionsPattern.matcher(part);

            if (titleMatcher.find() && descriptionMatcher.find() && dialoguesMatcher.find() && actionsMatcher.find()) {
                Scene scene = new Scene();
                scene.setSceneTitle(titleMatcher.group(1));
                scene.setSceneDescription(descriptionMatcher.group(1).trim());

                // 解析角色对话
                List<String> dialogues = new ArrayList<>();
                String[] dialogueLines = dialoguesMatcher.group(1).split("\n");
                for (String line : dialogueLines) {
                    dialogues.add(line.trim());
                }
                scene.setRoleDialogues(dialogues);

                // 解析动作指示
                List<String> actions = Arrays.asList(actionsMatcher.group(1).split("\n"));
                scene.setActionInstructions(actions.stream().map(String::trim).toList());

                scenes.add(scene);
            }
        }

        return scenes;
    }
}