package com.t1impulse.interviewer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SessionResponse(
        UUID sessionId,
        String description,
        LocalDateTime createdAt,
        List<TestGenerationResponse> tests,
        List<AlgorithmTaskInSessionResponse> algorithmTasks
) {
    public record AlgorithmTaskInSessionResponse(
            Long taskId,
            String titleRu,
            String descriptionRu,
            String difficulty,
            LocalDateTime assignedAt
    ) {
    }
}

