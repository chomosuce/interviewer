package com.t1impulse.interviewer.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO для парсинга JSON ответа от AI модели
 */
public record AiTestResponse(List<QuestionDto> questions) {

    public record QuestionDto(
            String text,
            Map<String, String> options,
            String correctAnswer
    ) {
    }
}

