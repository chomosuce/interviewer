package com.t1impulse.interviewer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.t1impulse.interviewer.dto.RunResponse;
import com.t1impulse.interviewer.dto.SubmitSolutionRequest;
import com.t1impulse.interviewer.service.SolutionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
public class CodeController {

    private final SolutionService solutionService;

    @PostMapping("/submit")
    public ResponseEntity<RunResponse> submitSolution(@RequestBody SubmitSolutionRequest request) {
        try {
            RunResponse response = solutionService.submitSolution(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
