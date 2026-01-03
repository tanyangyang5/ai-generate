package com.example.aigenerate.service;

import com.example.aigenerate.config.AiApiConfig;
import com.example.aigenerate.dto.ChatCompletionRequest;
import com.example.aigenerate.dto.ScriptParseRequest;
import com.example.aigenerate.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiScriptParsingService {

    @Autowired
    private AiApiConfig aiApiConfig;

    // 使用 OkHttp 客户端（可复用，线程安全）
    private final OkHttpClient okHttpClient ;
    private final ObjectMapper objectMapper ;

    public List<VisualAssetScene> parseScriptToStructuredParagraphs(ScriptParseRequest scriptParseRequest) {
        ChatCompletionRequest request = getChatCompletionRequest(scriptParseRequest.getModel(),scriptParseRequest.getScript(),scriptParseRequest.getUserStyle());

        // 构建 JSON 请求体
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("序列化请求失败", e);
        }

        // 构建 HTTP 请求
        Request httpRequest = new Request.Builder()
                .url(aiApiConfig.getUrl())
                .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")))
                .addHeader("Authorization", "Bearer " + aiApiConfig.getKey())
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("AI API 调用失败，状态码: " + response.code());
            }

            if (response.body() == null) {
                throw new RuntimeException("AI 返回结果为空");
            }

            String responseBody = response.body().string();
            ChatCompletionResponse aiResponse = objectMapper.readValue(responseBody, ChatCompletionResponse.class);

            if (aiResponse.getChoices() == null || aiResponse.getChoices().isEmpty()) {
                throw new RuntimeException("AI 返回结果中无 choices");
            }
            var message = aiResponse.getChoices().getFirst().getMessage();

            // 检查是否被拒绝
            if (message.getRefusal() != null && !message.getRefusal().isBlank()) {
                throw new RuntimeException("AI 拒绝生成内容: " + message.getRefusal());
            }

            if (message.getContent() == null || message.getContent().isBlank()) {
                throw new RuntimeException("AI 返回内容为空");
            }

            String structuredText = aiResponse.getChoices().getFirst().getMessage().getContent();
            return parseAiOutput(structuredText);

        } catch (IOException e) {
            throw new RuntimeException("调用 AI 接口失败: " + e.getMessage(), e);
        }
    }

    @NotNull
    private ChatCompletionRequest getChatCompletionRequest(String model,String scriptContent,String userStyle) {
        String systemPromptTemplate = """
                # Role
                你是一位专业的影视视觉资产总监。你的核心任务是将文本剧本拆解为可供 AI 生成工具（Midjourney/Stable Diffusion/Sora）使用的**四大类标准化资产**。

                # Global Style Instruction
                **必须严格基于以下风格进行视觉定义：**
                👉 **目标风格：%s**
                *(在此风格下，请统筹光影、材质、色彩饱和度与渲染质感。所有输出的视觉描述必须服务于此风格。)*

                # Task
                请阅读用户提供的剧本/小说片段，按场景为单位，输出以下四个模块的内容。
                **注意：** 若某类资产在该场景中未出现或无变化，可省略该模块，但必须保证结构完整。

                ### 模块一：风格化分镜脚本 (Storyboard Timeline)
                - 每一行画面描述必须以符号 `▲` 开头。
                - 内容格式：`▲[符合风格的运镜方式] + [画面主体与动态] + [风格化光影氛围]`
                - 对话格式：`角色名（情绪）：台词`

                ### 模块二：人物建模资产 (Character Asset)
                - 仅输出本场核心角色（首次出现时）。
                - 字段：
                  【角色名】
                  【风格化造型】
                  【面部/特征】
                  【服饰/材质】

                ### 模块三：场景资产库 (Scene Asset - Empty Set)
                - 字段：
                  【场景名称】
                  【风格渲染词】
                  【光影/色调】
                  【环境/陈设】

                ### 模块四：道具资产库 (Props Asset - Key Items)
                - 仅提取剧情关键物品。
                - 字段：
                  【道具名称】
                  【类别】
                  【视觉详情】
                  【风格化质感】

                # Output Rules
                - 每个场景独立输出，**不要编号**。
                - 按顺序输出四个模块（即使某些为空，也跳过不写标题）。
                - 模块标题格式：
                  ```
                  **1. 风格化分镜脚本**
                  ...
                  **2. 人物资产：[角色名]**
                  ...
                  **3. 场景资产：[场景名称]**
                  ...
                  **4. 道具资产：[道具名称]**
                  ```
                - 场景之间用 `---` 分隔（前后无空行）。
                - **禁止任何解释、前缀、后缀或总结**。
                """;
        String systemPrompt = String.format(systemPromptTemplate, userStyle);
        List<ChatCompletionRequest.Message> messages = Arrays.asList(
                new ChatCompletionRequest.Message("system", systemPrompt),
                new ChatCompletionRequest.Message("user", scriptContent)
        );
        return new ChatCompletionRequest(model, messages);
    }



    private String extractValue(String line) {
        int idx = line.indexOf("】");
        if (idx != -1 && idx + 1 < line.length()) {
            return line.substring(idx + 1).trim();
        }
        return "";
    }

    public List<VisualAssetScene> parseAiOutput(String aiResponseText) {
        List<VisualAssetScene> scenes = new ArrayList<>();
        String[] blocks = aiResponseText.split("\\s*---\\s*");

        for (String block : blocks) {
            if (block.trim().isEmpty()) continue;

            VisualAssetScene scene = new VisualAssetScene();
            StoryboardTimeline storyboard = new StoryboardTimeline();
            List<String> shots = new ArrayList<>();
            List<String> dialogs = new ArrayList<>();

            // 按行扫描
            String[] lines = block.split("\n");
            int i = 0;

            // === 模块一：分镜脚本 ===
            while (i < lines.length && !lines[i].trim().startsWith("**2.")) {
                String line = lines[i].trim();
                if (line.startsWith("▲")) {
                    shots.add(line);
                } else if (line.contains("：")) { // 中文冒号
                    dialogs.add(line);
                }
                i++;
            }
            storyboard.setShotLines(shots);
            storyboard.setDialogues(dialogs);
            scene.setStoryboard(storyboard);

            // === 模块二：人物资产 ===
            if (i < lines.length && lines[i].contains("**2. 人物资产：")) {
                String roleName = extractBetween(lines[i], "**2. 人物资产：", "**");
                CharacterAsset charAsset = new CharacterAsset();
                charAsset.setRoleName(roleName);
                i++;
                charAsset.setStylizedLook(extractField(lines, i, "【风格化造型】"));
                charAsset.setFacialFeatures(extractField(lines, i, "【面部/特征】"));
                charAsset.setCostumeMaterial(extractField(lines, i, "【服饰/材质】"));
                scene.setCharacter(charAsset);
                // 跳过已读行（简化处理，假设每字段一行）
                while (i < lines.length && !lines[i].trim().startsWith("**3.")) i++;
            }

            // === 模块三：场景资产 ===
            if (i < lines.length && lines[i].contains("**3. 场景资产：")) {
                String sceneName = extractBetween(lines[i], "**3. 场景资产：", "**");
                SceneAsset sceneAsset = new SceneAsset();
                sceneAsset.setSceneName(sceneName);
                i++;
                sceneAsset.setStyleRenderKeywords(extractField(lines, i, "【风格渲染词】"));
                sceneAsset.setLightingTone(extractField(lines, i, "【光影/色调】"));
                sceneAsset.setEnvironmentProps(extractField(lines, i, "【环境/陈设】"));
                scene.setSceneAsset(sceneAsset);
                while (i < lines.length && !lines[i].trim().startsWith("**4.")) i++;
            }

            // === 模块四：道具资产 ===
            if (i < lines.length && lines[i].contains("**4. 道具资产：")) {
                String propName = extractBetween(lines[i], "**4. 道具资产：", "**");
                PropAsset propAsset = new PropAsset();
                propAsset.setPropName(propName);
                i++;
                propAsset.setCategory(extractField(lines, i, "【类别】"));
                propAsset.setVisualDetails(extractField(lines, i, "【视觉详情】"));
                propAsset.setStylizedTexture(extractField(lines, i, "【风格化质感】"));
                scene.setProp(propAsset);
            }

            scenes.add(scene);
        }

        return scenes;
    }

    // 辅助方法
    private String extractBetween(String line, String start, String end) {
        int s = line.indexOf(start);
        if (s == -1) return "";
        s += start.length();
        int e = line.indexOf(end, s);
        if (e == -1) e = line.length();
        return line.substring(s, e).trim();
    }

    private String extractField(String[] lines, int startIndex, String label) {
        for (int i = startIndex; i < lines.length; i++) {
            if (lines[i].trim().startsWith(label)) {
                return lines[i].substring(label.length()).trim();
            }
            if (lines[i].trim().startsWith("**")) break; // 到下一模块
        }
        return "";
    }
}