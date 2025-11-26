package com.t1impulse.interviewer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.t1impulse.interviewer.config.TestTopic;
import com.t1impulse.interviewer.dto.CreateSessionRequest;
import com.t1impulse.interviewer.dto.SessionResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse;
import com.t1impulse.interviewer.dto.TopicResponse;
import com.t1impulse.interviewer.service.SessionService;
import com.t1impulse.interviewer.service.TestGenerationService;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionContorller {
    private final TestGenerationService testGenerationService;
    private final SessionService sessionService;

    @PostMapping("/create")
    public SessionResponse createSession(@RequestBody(required = false) CreateSessionRequest request) {
        return sessionService.createSession(request);
    }

    @GetMapping("/get/{sessionId}")
    public SessionResponse getSession(@PathVariable UUID sessionId) {
        return sessionService.getSession(sessionId);
    }

    @GetMapping("/allSessions")
    public List<SessionResponse> getAllSessions() {
        return sessionService.getAllSessions();
    }


    @GetMapping("/createTest")
    public TestGenerationResponse generateTest(
            @RequestParam TestTopic topic,
            @RequestParam(defaultValue = "5") int questionCount,
            @RequestParam(required = false) UUID sessionId) {
        return testGenerationService.generateTest(topic, questionCount, sessionId);
    }

    @GetMapping("/getTest/{testId}")
    public TestGenerationResponse getTestById(@PathVariable Long testId) {
        return testGenerationService.getTestById(testId);
    }

    @GetMapping("/allTests")
    public List<TestGenerationResponse> getTestsByTopic(@RequestParam TestTopic topic) {
        return testGenerationService.getTestsByTopic(topic);
    }

    @GetMapping("/topics")
    public List<TopicResponse> getAllTopics() {
        return Arrays.stream(TestTopic.values())
                .map(topic -> new TopicResponse(
                        topic.name(),
                        topic.getDisplayName(),
                        topic.getDescription()
                ))
                .toList();
    }
}
