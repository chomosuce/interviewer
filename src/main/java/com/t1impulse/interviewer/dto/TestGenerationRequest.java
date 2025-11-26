package com.t1impulse.interviewer.dto;

import com.t1impulse.interviewer.config.TestTopic;

public record TestGenerationRequest(
        TestTopic topic,
        Integer questionCount
) {
    private static final int DEFAULT_QUESTION_COUNT = 5;
    private static final int MIN_QUESTION_COUNT = 1;
    private static final int MAX_QUESTION_COUNT = 20;

    public int getQuestionCount() {
        if (questionCount == null) {
            return DEFAULT_QUESTION_COUNT;
        }
        return Math.max(MIN_QUESTION_COUNT, Math.min(questionCount, MAX_QUESTION_COUNT));
    }
}

