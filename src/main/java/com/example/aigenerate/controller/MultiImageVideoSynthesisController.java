package com.example.aigenerate.controller;


import com.example.aigenerate.dto.MultiImageVideoSynthesisRequest;
import com.example.aigenerate.service.MultiImageVideoSynthesisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/multiImage/Video")
public class MultiImageVideoSynthesisController {

    private final MultiImageVideoSynthesisService multiImageVideoSynthesisService;

    public MultiImageVideoSynthesisController(MultiImageVideoSynthesisService multiImageVideoSynthesisService) {
        this.multiImageVideoSynthesisService = multiImageVideoSynthesisService;
    }

    @PostMapping("/synthesize")
    public ResponseEntity<String> synthesizeVideo(@RequestBody MultiImageVideoSynthesisRequest request) throws IOException {
        String response = multiImageVideoSynthesisService.submitTask(request);
        return ResponseEntity.ok(response);
    }
}