package com.t1impulse.interviewer.dto;

import com.t1impulse.interviewer.config.TestTopic;

public record TestGenerationResponse(
        TestTopic topic,
        int questionCount,
        String content
) {
}

