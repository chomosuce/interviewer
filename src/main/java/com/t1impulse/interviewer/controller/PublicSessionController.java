package com.t1impulse.interviewer.controller;

import com.t1impulse.interviewer.dto.ResultsResponse;
import com.t1impulse.interviewer.dto.SessionResponse;
import com.t1impulse.interviewer.dto.StartSessionResponse;
import com.t1impulse.interviewer.dto.SubmitResultsRequest;
import com.t1impulse.interviewer.service.ResultsService;
import com.t1impulse.interviewer.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/session")
@RequiredArgsConstructor
public class PublicSessionController {

    private final SessionService sessionService;
    private final ResultsService resultsService;

    @GetMapping("/{accessToken}")
    public ResponseEntity<SessionResponse> getSessionByToken(@PathVariable String accessToken) {
        try {
            SessionResponse session = sessionService.getSessionByToken(accessToken);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{accessToken}/start")
    public ResponseEntity<StartSessionResponse> startSession(@PathVariable String accessToken) {
        try {
            StartSessionResponse response = sessionService.startSession(accessToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{accessToken}/results")
    public ResponseEntity<ResultsResponse> submitResults(
            @PathVariable String accessToken,
            @RequestBody SubmitResultsRequest request) {
        try {
            ResultsResponse response = resultsService.submitResults(accessToken, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

