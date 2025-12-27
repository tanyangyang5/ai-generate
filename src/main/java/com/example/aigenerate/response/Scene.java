package com.example.aigenerate.response;

import lombok.Data;

import java.util.List;

@Data
public class Scene {
    private String sceneTitle; // 场景标题
    private String sceneDescription; // 场景描述
    private List<String> roleDialogues; // 角色对话列表
    private List<String> actionInstructions; // 动作指示列表
}