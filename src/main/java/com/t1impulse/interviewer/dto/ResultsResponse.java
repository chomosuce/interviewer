package com.t1impulse.interviewer.dto;

import java.time.LocalDateTime;

public record ResultsResponse(
        Long id,
        Long candidateId,
        String candidateName,
        String testResults,
        String algorithmResults,
        Boolean violationDetected,
        LocalDateTime submittedAt
) {
}

