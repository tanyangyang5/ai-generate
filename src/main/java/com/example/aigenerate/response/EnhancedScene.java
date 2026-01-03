package com.example.aigenerate.response;

import lombok.Data;

import java.util.List;

@Data
public class EnhancedScene {
    private SceneVisual visual;
    private List<CharacterProfile> characters;
    private ScriptContent script;
}