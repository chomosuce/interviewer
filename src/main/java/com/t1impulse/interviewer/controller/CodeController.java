package com.t1impulse.interviewer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.t1impulse.interviewer.dto.RunRequest;
import com.t1impulse.interviewer.dto.RunResponse;
import com.t1impulse.interviewer.service.CodeRunnerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/run")
@RequiredArgsConstructor
public class CodeController {

    private final CodeRunnerService codeRunnerService;

    @PostMapping
    public RunResponse run(@RequestBody RunRequest request) {
        return codeRunnerService.run(request);
    }
}
