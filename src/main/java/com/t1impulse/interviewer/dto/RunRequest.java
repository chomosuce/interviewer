package com.t1impulse.interviewer.dto;

import java.util.List;

import com.t1impulse.interviewer.config.Language;

public record RunRequest(
        Language language,
        String source,
        List<TestDto> tests,
        int timeLimitMs,
        int memoryLimitMb) {
}