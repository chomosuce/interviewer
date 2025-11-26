package com.t1impulse.interviewer.dto;

public record StartSessionResponse(
        boolean alreadyStarted,
        String message
) {
}

