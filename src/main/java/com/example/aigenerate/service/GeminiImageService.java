// GeminiImageService.java
package com.example.aigenerate.service;

import com.example.aigenerate.response.GeminiImageResponse;
import com.example.aigenerate.response.textToImage.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiImageService {
    // 使用 OkHttp 客户端（可复用，线程安全）
    private final OkHttpClient okHttpClient ;
    private final ObjectMapper objectMapper ;
    @Value("${ezlinkai.api.key}")
    private String apiKey;
    @Value("${ezlinkai.api.geminiImageUrl}")
    private String imageUrl;


    // ===== 新增方法：从剧本生成全套视觉资产图像 =====
    public List<String> generateVisualAssetsFromScript(String userStyle, String prompt) {


        String jsonRequest = """
        {
          "contents": [{
            "parts": [{ "text": "%s" }]
          }]
        }
        """.formatted(prompt+",风格"+userStyle+",生成一张三视图");

        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(imageUrl)
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            log.info(response.body().string());
            if (!response.isSuccessful()) {
                assert response.body() != null;
                log.info(response.body().string());
                log.info(response.message());
                throw new RuntimeException("Unexpected code " + response);
            }

            // 解析JSON响应
            assert response.body() != null;
            GeminiImageResponse geminiImageResponse = objectMapper.readValue(response.body().string(), GeminiImageResponse.class);
            return extractBase64Images(geminiImageResponse);
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                String error = response.body() != null ? response.body().string() : "No body";
//                throw new RuntimeException("LLM 解析失败: " + response.code() + " - " + error);
//            }
//
//            String responseBody = response.body().string();
//            GeminiImageResponse aiResponse = objectMapper.readValue(responseBody, GeminiImageResponse.class);
//            String parsedText = extractTextResponse(aiResponse); // 新增辅助方法
//
//            // Step 2: 解析文本为结构化资产
//            List<VisualAssetSceneTextToImage> assetScenes = parseAssetScenes(parsedText);
//
//            // Step 3: 为每个非空资产生成图像
//            List<VisualAssetImages> result = new ArrayList<>();
//            for (VisualAssetSceneTextToImage scene : assetScenes) {
//                VisualAssetImages imgScene = new VisualAssetImages();
//
//                // 分镜脚本 → 文生图（可选，或留空）
//                if (scene.getStoryboard() != null && scene.getStoryboard().getRawText() != null) {
//                    // 可选：将分镜文本转为视频提示，此处暂不生成图
//                    imgScene.setStoryboardPrompt(scene.getStoryboard().getRawText());
//                }
//
//                // 角色三视图 → 必须生成
//                if (scene.getCharacter() != null) {
//                    String charPrompt = buildCharacterPrompt(scene.getCharacter(), userStyle);
//                    List<String> charImages = textGenerateImages(charPrompt);
//                    imgScene.setCharacterImages(charImages);
//                    imgScene.setCharacterPrompt(charPrompt);
//                }
//
//                // 场景空镜
//                if (scene.getScene() != null) {
//                    String scenePrompt = buildScenePrompt(scene.getScene(), userStyle);
//                    List<String> sceneImages = textGenerateImages(scenePrompt);
//                    imgScene.setSceneImages(sceneImages);
//                    imgScene.setScenePrompt(scenePrompt);
//                }
//
//                // 道具特写
//                if (scene.getProp() != null) {
//                    String propPrompt = buildPropPrompt(scene.getProp(), userStyle);
//                    List<String> propImages = textGenerateImages(propPrompt);
//                    imgScene.setPropImages(propImages);
//                    imgScene.setPropPrompt(propPrompt);
//                }
//
//                result.add(imgScene);
//            }
//
//            return result;

        } catch (Exception e) {
            throw new RuntimeException("生成视觉资产失败", e);
        }
    }

    // ===== 辅助方法：从 Gemini 响应中提取纯文本 =====
    private String extractTextResponse(GeminiImageResponse response) {
        return response.getCandidates().stream()
                .flatMap(c -> c.getContent().getParts().stream())
                .filter(part -> part.getText() != null)
                .map(part -> part.getText())
                .collect(Collectors.joining("\n"));
    }

    // ===== 辅助方法：解析 LLM 输出为结构化资产 =====
    private List<VisualAssetSceneTextToImage> parseAssetScenes(String llmOutput) {
        // 此处可复用你之前写的 parseAiOutput 逻辑（略，因篇幅省略）
        // 返回 List<VisualAssetScene>
        throw new UnsupportedOperationException("请实现文本解析逻辑");
    }

    // ===== 构建角色三视图提示词（关键！）=====
    private String buildCharacterPrompt(CharacterSheetModule charMod, String userStyle) {
        return String.format(
                "%s, %s. %s. %s. %s",
                charMod.getCompositionView(),      // ← 强制包含三视图关键词
                userStyle,
                charMod.getStylizedLook(),
                charMod.getAppearance(),
                charMod.getCostumeGear()
        );
    }

    private String buildScenePrompt(SceneAssetModule sceneMod, String userStyle) {
        return String.format(
                "%s, %s, %s, empty scene, no characters, white background optional",
                sceneMod.getSceneName(),
                sceneMod.getStyleRenderKeywords(),
                sceneMod.getEnvironment()
        );
    }

    private String buildPropPrompt(PropAssetModule propMod, String userStyle) {
        return String.format(
                "%s, %s, %s, isolated on white background, product shot, high detail",
                propMod.getPropName(),
                propMod.getVisualDetails(),
                userStyle
        );
    }































    public List<String> textGenerateImages(String userStyle,String prompt){
        // 构造请求体
//        String jsonRequest = """
//        {
//          "contents": [{
//            "parts": [{ "text": "%s" }]
//          }]
//        }
//        """.formatted(prompt);



//        String jsonRequest = """
//        {
//          "contents": [{
//            "parts": [{ "text": "%s" }]
//          }],
//        }
//        """.formatted(prompt+",风格"+userStyle+",生成一张三视图");

//  "aspectRatio": "5:4",
        String jsonRequest = """
        {
          "contents": [{
            "parts": [{ "text": "%s" }]
          }],
          "generationConfig": {
            "responseModalities": ["TEXT"],
            "imageConfig": {
              "imageSize": "1280x720"
            }
          }
        }
        """.formatted(prompt);

        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(imageUrl)
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                log.info(response.body().string());
                log.info(response.message());
                throw new RuntimeException("Unexpected code " + response);
            }

            // 解析JSON响应
            assert response.body() != null;
            GeminiImageResponse geminiImageResponse = objectMapper.readValue(response.body().string(), GeminiImageResponse.class);
            return extractBase64Images(geminiImageResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> extractBase64Images(GeminiImageResponse apiResponse) {
        return apiResponse.getCandidates().stream()
                .flatMap(candidate -> candidate.getContent().getParts().stream())
                .filter(part -> part.getInlineData() != null && part.getInlineData().getData() != null)
                .map(part -> "data:" + part.getInlineData().getMimeType() + ";base64," + part.getInlineData().getData())
                .collect(Collectors.toList());
    }


    public List<String> editImage(
            String prompt,
            List<String> base64Images,
            String aspectRatio,
            String imageSize)  {

        if (base64Images == null || base64Images.isEmpty()) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        // 构建 parts：第一个是 text，后面全是 inline_data
        StringBuilder partsBuilder = new StringBuilder();
        partsBuilder.append("{\"text\": \"").append(prompt.replace("\"", "\\\"")).append("\"}");

        for (String img : base64Images) {
            // 转义 Base64 中可能存在的特殊字符（虽然 Base64 通常安全，但保险起见）
            String safeBase64 = img.replace("\\", "\\\\").replace("\"", "\\\"");
            partsBuilder.append(",{\"inline_data\":{\"mime_type\":\"image/png\",\"data\":\"")
                    .append(safeBase64)
                    .append("\"}}");
        }

        String jsonBody = """
        {
          "contents": [{
            "parts": [%s]
          }],
          "generationConfig": {
            "responseModalities": ["TEXT", "IMAGE"],
            "imageConfig": {
              "aspectRatio": "%s",
              "imageSize": "%s"
            }
          }
        }
        """.formatted(partsBuilder.toString(), aspectRatio, imageSize);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(imageUrl)
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new RuntimeException("API Error: " + response.code() + " - " + errorBody);
            }
            String responseBody = response.body().string();
            GeminiImageResponse apiResponse = objectMapper.readValue(responseBody, GeminiImageResponse.class);
            return extractBase64Images(apiResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}