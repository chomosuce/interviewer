package com.t1impulse.interviewer.controller;

import com.t1impulse.interviewer.dto.SessionResponse;
import com.t1impulse.interviewer.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/session")
@RequiredArgsConstructor
public class PublicSessionController {

    private final SessionService sessionService;

    @GetMapping("/{accessToken}")
    public ResponseEntity<SessionResponse> getSessionByToken(@PathVariable String accessToken) {
        try {
            SessionResponse session = sessionService.getSessionByToken(accessToken);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

