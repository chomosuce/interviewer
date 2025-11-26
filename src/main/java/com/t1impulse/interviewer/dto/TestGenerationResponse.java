package com.t1impulse.interviewer.dto;

import com.t1impulse.interviewer.config.TestTopic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TestGenerationResponse(
        Long testId,
        UUID sessionId,
        TestTopic topic,
        int questionCount,
        LocalDateTime createdAt,
        List<QuestionResponse> questions
) {

    public record QuestionResponse(
            Long id,
            String text,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctAnswer
    ) {
    }
}
