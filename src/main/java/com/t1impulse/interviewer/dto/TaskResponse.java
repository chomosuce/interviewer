package com.t1impulse.interviewer.dto;

import com.t1impulse.interviewer.entity.Difficulty;

import java.util.UUID;

public record TaskResponse(
        Long id,
        String titleRu,
        String descriptionRu,
        Difficulty difficulty,
        UUID sessionId
) {
}

