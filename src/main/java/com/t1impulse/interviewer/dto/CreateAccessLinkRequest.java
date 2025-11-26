package com.t1impulse.interviewer.dto;

import java.util.UUID;

public record CreateAccessLinkRequest(
        UUID sessionId,
        String candidateName
) {
}

