package com.example.aigenerate.controller;


import com.example.aigenerate.dto.VideoSynthesisDto;
import com.example.aigenerate.service.TaskQueryService;
import com.example.aigenerate.service.VideoSynthesisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashscope/video")
@Tag(name = "视频合成", description = "AI 视频生成接口")
public class VideoController {

    @Autowired
    private VideoSynthesisService videoSynthesisService;

    @Autowired
    private TaskQueryService taskQueryService;

    /**
     * 提交视频生成任务
     */
    @PostMapping("/submit")
    @Operation(summary = "提交视频任务")
    public ResponseEntity<?> submit(@RequestBody VideoSynthesisDto.Request request) {
        try {
            VideoSynthesisDto.Response response = videoSynthesisService.submitTask(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("提交失败: " + e.getMessage());
        }
    }

    /**
     * 查询任务状态
     */
    @Operation(summary = "查询任务状态")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getTaskStatus(@PathVariable String taskId) {
        try {
            VideoSynthesisDto.Response response = taskQueryService.queryTask(taskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("查询失败: " + e.getMessage());
        }
    }
}