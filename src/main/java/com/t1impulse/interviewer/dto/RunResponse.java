package com.t1impulse.interviewer.dto;

import java.util.List;

public record RunResponse(
        String status, // OK / COMPILATION_ERROR / PARTIAL / RUNTIME_ERROR
        String compileError,
        String runtimeError,
        List<TestResultDto> results) {
}
