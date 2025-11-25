package com.t1impulse.interviewer.dto;

public record ExecResult(
        int exitCode,
        String stdout,
        String stderr,
        boolean timeout) {
}