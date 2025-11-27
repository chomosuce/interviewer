package com.t1impulse.interviewer.dto;

public record CodeReviewRequest(
        Long taskId,
        String codeSnippet
) {
}

