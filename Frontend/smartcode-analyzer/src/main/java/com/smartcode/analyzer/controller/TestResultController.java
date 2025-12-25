package com.smartcode.analyzer.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TestResultController {

    private SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    @PostMapping("/api/test-result")
    public String receiveTestResult(@RequestBody Map<String, String> result) {
        try {
            emitter.send(
                SseEmitter.event()
                    .name("test-update")
                    .data(result)
            );
        } catch (IOException e) {
            emitter = new SseEmitter(Long.MAX_VALUE);
        }
        return "Result received";
    }

    @GetMapping("/api/events")
    public SseEmitter streamEvents() {
        return emitter;
    }
}
