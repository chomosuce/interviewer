package com.t1impulse.interviewer.dto;

public record TestResultDto(
        int testIndex,
        String status, // OK / WRONG_ANSWER / TLE / RUNTIME_ERROR
        String expected,
        String got,
        String stderr) {
}