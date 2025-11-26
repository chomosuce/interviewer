package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.ResultsResponse;
import com.t1impulse.interviewer.dto.SubmitResultsRequest;
import com.t1impulse.interviewer.entity.SessionCandidate;
import com.t1impulse.interviewer.entity.SessionResult;
import com.t1impulse.interviewer.repository.SessionCandidateRepository;
import com.t1impulse.interviewer.repository.SessionResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultsService {

    private final SessionResultRepository sessionResultRepository;
    private final SessionCandidateRepository sessionCandidateRepository;

    @Transactional
    public ResultsResponse submitResults(String accessToken, SubmitResultsRequest request) {
        SessionCandidate candidate = sessionCandidateRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid access token"));

        SessionResult result = SessionResult.builder()
                .candidate(candidate)
                .testResults(request.testResults())
                .algorithmResults(request.algorithmResults())
                .violationDetected(request.violationDetected() != null ? request.violationDetected() : false)
                .build();

        SessionResult saved = sessionResultRepository.save(result);
        log.info("Results submitted for candidate {} with token {}", candidate.getCandidateName(), accessToken);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ResultsResponse> getResultsBySessionId(UUID sessionId) {
        List<SessionResult> results = sessionResultRepository.findByCandidate_SessionId(sessionId);
        return results.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultsResponse> getResultsByCandidateId(Long candidateId) {
        List<SessionResult> results = sessionResultRepository.findByCandidateId(candidateId);
        return results.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultsResponse> getResultsByAccessToken(String accessToken) {
        // Проверяем валидность токена
        sessionCandidateRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid access token"));
        
        List<SessionResult> results = sessionResultRepository.findByCandidate_AccessToken(accessToken);
        return results.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ResultsResponse mapToResponse(SessionResult result) {
        SessionCandidate candidate = result.getCandidate();
        return new ResultsResponse(
                result.getId(),
                candidate.getId(),
                candidate.getCandidateName(),
                result.getTestResults(),
                result.getAlgorithmResults(),
                result.getViolationDetected(),
                result.getSubmittedAt()
        );
    }
}

