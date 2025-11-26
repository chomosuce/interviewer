package com.t1impulse.interviewer.dto;

import java.time.LocalDateTime;

public record CandidateResponse(
        Long id,
        String candidateName,
        String accessToken,
        String accessUrl,
        LocalDateTime createdAt
) {
}

