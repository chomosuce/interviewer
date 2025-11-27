package com.t1impulse.interviewer.controller;

import com.t1impulse.interviewer.dto.CodeReviewRequest;
import com.t1impulse.interviewer.dto.CodeReviewResponse;
import com.t1impulse.interviewer.service.CodeReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/code")
@RequiredArgsConstructor
public class PublicCodeController {

    private final CodeReviewService codeReviewService;

    @PostMapping("/review")
    public ResponseEntity<CodeReviewResponse> reviewCode(@RequestBody CodeReviewRequest request) {
        try {
            CodeReviewResponse response = codeReviewService.reviewCode(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

