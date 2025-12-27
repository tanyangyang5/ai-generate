package com.example.aigenerate.controller;

import com.example.aigenerate.dto.ScriptParseRequest;
import com.example.aigenerate.response.Scene;
import com.example.aigenerate.service.AiScriptParsingService;
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
    public ResponseEntity<List<Scene>> parseScript(@RequestBody ScriptParseRequest request) {
        return ResponseEntity.ok(parsingService.parseScriptToStructuredParagraphs(request.getScript()));
    }
}