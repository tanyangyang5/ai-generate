package com.example.aigenerate.response;

import lombok.Data;

import java.util.List;

@Data
public class ScriptContent {
    private List<String> dialogues;      // "辛弃疾：杀贼！"
    private List<String> actionNotes;    // "抬手握拳"、"眼神结冰"
}