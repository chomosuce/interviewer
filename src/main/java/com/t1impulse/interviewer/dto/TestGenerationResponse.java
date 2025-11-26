package com.t1impulse.interviewer.dto;

import com.t1impulse.interviewer.config.TestTopic;

import java.time.LocalDateTime;
import java.util.List;

public record TestGenerationResponse(
        Long testId,
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
