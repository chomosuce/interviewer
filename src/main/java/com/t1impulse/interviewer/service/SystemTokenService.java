package com.t1impulse.interviewer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SystemTokenService {

    @Value("${app.system.token:}")
    private String systemToken;

    public void validateToken(String providedToken) {
        if (systemToken == null || systemToken.isEmpty()) {
            return;
        }

        if (providedToken == null || !systemToken.equals(providedToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. System token required.");
        }
    }
}

