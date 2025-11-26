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
import com.t1impulse.interviewer.dto.AccessLinkResponse;
import com.t1impulse.interviewer.dto.CandidateResponse;
import com.t1impulse.interviewer.dto.CreateAccessLinkRequest;
import com.t1impulse.interviewer.dto.CreateSessionRequest;
import com.t1impulse.interviewer.dto.ResultsResponse;
import com.t1impulse.interviewer.dto.SessionResponse;
import com.t1impulse.interviewer.dto.TaskResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse;
import com.t1impulse.interviewer.dto.TopicResponse;
import com.t1impulse.interviewer.entity.Difficulty;
import com.t1impulse.interviewer.service.ResultsService;
import com.t1impulse.interviewer.service.SessionService;
import com.t1impulse.interviewer.service.TaskService;
import com.t1impulse.interviewer.service.TestGenerationService;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SessionContorller {
    private final TestGenerationService testGenerationService;
    private final SessionService sessionService;
    private final TaskService taskService;
    private final ResultsService resultsService;

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

    @GetMapping("/randomTask")
    public ResponseEntity<TaskResponse> getRandomTask(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) UUID sessionId
    ) {
        try {
            TaskResponse task;
            if (difficulty != null) {
                task = taskService.getRandomTaskByDifficulty(difficulty, sessionId);
            } else {
                task = taskService.getRandomTask(sessionId);
            }
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/createAccessLink")
    public AccessLinkResponse createAccessLink(@RequestBody CreateAccessLinkRequest request) {
        return sessionService.createAccessLink(request);
    }

    @GetMapping("/candidates/{sessionId}")
    public List<CandidateResponse> getSessionCandidates(@PathVariable UUID sessionId) {
        return sessionService.getSessionCandidates(sessionId);
    }

    @GetMapping("/results/{sessionId}")
    public List<ResultsResponse> getSessionResults(@PathVariable UUID sessionId) {
        return resultsService.getResultsBySessionId(sessionId);
    }

    @GetMapping("/results/candidate/{candidateId}")
    public List<ResultsResponse> getCandidateResults(@PathVariable Long candidateId) {
        return resultsService.getResultsByCandidateId(candidateId);
    }
}
