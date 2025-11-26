package com.t1impulse.interviewer.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.t1impulse.interviewer.config.TestTopic;
import com.t1impulse.interviewer.dto.TestGenerationResponse;
import com.t1impulse.interviewer.service.ChatService;
import com.t1impulse.interviewer.service.TestGenerationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final TestGenerationService testGenerationService;

    @PostMapping
    public String chat(@RequestBody String question,
            HttpServletRequest request) {

        String convId = request.getHeader("X-Conv-Id");
        if (convId == null || convId.isBlank()) {
            convId = UUID.randomUUID().toString();
        }

        return chatService.ask(convId, question);
    }

    @GetMapping("/test")
    public TestGenerationResponse generateTest(
            @RequestParam TestTopic topic,
            @RequestParam(defaultValue = "5") int questionCount) {
        return testGenerationService.generateTest(topic, questionCount);
    }

    @GetMapping("/test/{testId}")
    public TestGenerationResponse getTestById(@PathVariable Long testId) {
        return testGenerationService.getTestById(testId);
    }

    @GetMapping("/tests")
    public java.util.List<TestGenerationResponse> getTestsByTopic(@RequestParam TestTopic topic) {
        return testGenerationService.getTestsByTopic(topic);
    }
}