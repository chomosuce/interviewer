package com.t1impulse.interviewer.dto;

public record SubmitResultsRequest(
        String testResults,
        String algorithmResults,
        Boolean violationDetected
) {
}

