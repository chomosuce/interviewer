package com.t1impulse.interviewer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SessionResponse(
        UUID sessionId,
        String description,
        LocalDateTime createdAt,
        List<TestGenerationResponse> tests
        // В будущем: List<AlgorithmTaskResponse> algorithmTasks
) {
}

