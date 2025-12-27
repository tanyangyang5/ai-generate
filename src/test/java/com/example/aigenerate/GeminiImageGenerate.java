package com.example.aigenerate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class GeminiImageGenerate {

    public static void main(String[] args) {
        try {
            // 替换为你的实际图片路径
            String img1Path = "/Users/tanyangyang/Downloads/tank1.png";
            String img2Path = "person2.png";

            String apiKey = "vHTLcWwdZGi9poKF9688Db08DeC74036937c96C42668030d"; // 你的 API Key
            String response = generateImage(apiKey, img1Path, img2Path);
            System.out.println("Response:\n" + response);

            // 可选：将返回的 JSON 写入文件
            Files.write(Paths.get("response.json"), response.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateImage(String apiKey, String img1Path, String img2Path) throws Exception {
        String url = "https://api.ezlinkai.com/v1beta/models/gemini-3-pro-image-preview:generateContent";

        // 读取并 Base64 编码图片
        String img1Base64 = encodeFileToBase64(img1Path);
//        String img2Base64 = encodeFileToBase64(img2Path);

        // 构建 JSON 请求体
        String jsonPayload = String.format(
                "{\n" +
                        "  \"contents\": [\n" +
                        "    {\n" +
                        "      \"parts\": [\n" +
                        "        {\"text\": \"将这张坦克变为一张会飞的坦克.\"},\n" +
                        "        {\"inline_data\": {\"mime_type\": \"image/png\", \"data\": \"%s\"}}\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"generationConfig\": {\n" +
                        "    \"responseModalities\": [\"TEXT\", \"IMAGE\"],\n" +
                        "    \"imageConfig\": {\n" +
                        "      \"aspectRatio\": \"5:4\",\n" +
                        "      \"imageSize\": \"2K\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                escapeJson(img1Base64)
        );

        // 发送 HTTP POST 请求
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-goog-api-key", apiKey);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream();
        return readInputStream(is);
    }

    private static String encodeFileToBase64(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String escapeJson(String value) {
        // 转义 JSON 中的特殊字符（尤其是双引号和反斜杠）
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private static String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}