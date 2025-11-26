package com.t1impulse.interviewer.dto;

public record AccessLinkResponse(
        String accessToken,
        String accessUrl,
        String candidateName
) {
}

