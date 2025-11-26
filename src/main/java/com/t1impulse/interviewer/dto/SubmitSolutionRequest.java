package com.t1impulse.interviewer.dto;

import com.t1impulse.interviewer.config.Language;

public record SubmitSolutionRequest(
        Long taskId,
        Language language,
        String source
) {
}

