package com.example.aigenerate.controller;

import com.example.aigenerate.dto.ScriptParseRequest;
import com.example.aigenerate.response.EnhancedScene;
import com.example.aigenerate.response.Scene;
import com.example.aigenerate.response.VisualAssetScene;
import com.example.aigenerate.service.AiScriptParsingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/script")
public class ScriptController {

    @Autowired
    private AiScriptParsingService parsingService;

    @PostMapping("/parse")
    @Operation(summary = "将剧本片段解析为四大AI视觉资产",
            description = "基于指定艺术风格（如赛博朋克、水墨国风等），生成可直接用于Sora/Midjourney的标准化资产")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回场景资产列表"),
            @ApiResponse(responseCode = "400", description = "输入参数无效"),
            @ApiResponse(responseCode = "502", description = "AI服务调用失败")
    })

    public ResponseEntity<List<VisualAssetScene>> parseScript(@RequestBody ScriptParseRequest request) {
        return ResponseEntity.ok(parsingService.parseScriptToStructuredParagraphs(request));
    }

    /**
     * 剧本段落信息的提炼和优化
     */



}