package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.AccessLinkResponse;
import com.t1impulse.interviewer.dto.CandidateResponse;
import com.t1impulse.interviewer.dto.CreateAccessLinkRequest;
import com.t1impulse.interviewer.dto.CreateSessionRequest;
import com.t1impulse.interviewer.dto.SessionResponse;
import com.t1impulse.interviewer.dto.SessionResponse.AlgorithmTaskInSessionResponse;
import com.t1impulse.interviewer.dto.StartSessionResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse.QuestionResponse;
import com.t1impulse.interviewer.entity.InterviewSession;
import com.t1impulse.interviewer.entity.SessionCandidate;
import com.t1impulse.interviewer.repository.InterviewSessionRepository;
import com.t1impulse.interviewer.repository.SessionCandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final InterviewSessionRepository sessionRepository;
    private final SessionCandidateRepository sessionCandidateRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        InterviewSession session = InterviewSession.builder()
                .description(request != null ? request.description() : null)
                .build();
        
        InterviewSession saved = sessionRepository.save(session);
        log.info("Created new session with id: {}", saved.getId());
        
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        return mapToResponse(session);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CandidateResponse> getSessionCandidates(UUID sessionId) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        
        return session.getCandidates().stream()
                .map(candidate -> new CandidateResponse(
                        candidate.getId(),
                        candidate.getCandidateName(),
                        candidate.getAccessToken(),
                        baseUrl + "/api/public/session/" + candidate.getAccessToken(),
                        candidate.getCreatedAt()
                ))
                .toList();
    }

    public InterviewSession getOrCreateSession(UUID sessionId) {
        if (sessionId != null) {
            return sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        }
        // Создаём новую сессию если не передан ID
        InterviewSession session = InterviewSession.builder().build();
        return sessionRepository.save(session);
    }

    @Transactional
    public AccessLinkResponse createAccessLink(CreateAccessLinkRequest request) {
        InterviewSession session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + request.sessionId()));
        
        // Генерируем уникальный токен
        String accessToken = generateUniqueToken();
        
        // Создаем нового кандидата для сессии
        SessionCandidate candidate = SessionCandidate.builder()
                .session(session)
                .accessToken(accessToken)
                .candidateName(request.candidateName())
                .build();
        
        sessionCandidateRepository.save(candidate);
        session.addCandidate(candidate);
        
        String accessUrl = baseUrl + "/api/public/session/" + accessToken;
        
        log.info("Created access link for session {} with candidate name: {}", request.sessionId(), request.candidateName());
        
        return new AccessLinkResponse(accessToken, accessUrl, request.candidateName());
    }

    @Transactional(readOnly = true)
    public SessionResponse getSessionByToken(String accessToken) {
        SessionCandidate candidate = sessionCandidateRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid access token"));
        
        InterviewSession session = candidate.getSession();
        return mapToResponse(session);
    }

    @Transactional
    public StartSessionResponse startSession(String accessToken) {
        SessionCandidate candidate = sessionCandidateRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid access token"));
        
        if (Boolean.TRUE.equals(candidate.getStarted())) {
            // Сессия уже была начата
            return new StartSessionResponse(
                    true,
                    "Session was already started"
            );
        }
        
        // Отмечаем начало выполнения
        candidate.setStarted(true);
        sessionCandidateRepository.save(candidate);
        
        log.info("Session started for candidate {} with token {}", candidate.getCandidateName(), accessToken);
        
        return new StartSessionResponse(
                false,
                "Session started successfully"
        );
    }

    private String generateUniqueToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        // Проверяем уникальность (хотя вероятность коллизии очень мала)
        while (sessionCandidateRepository.findByAccessToken(token).isPresent()) {
            secureRandom.nextBytes(randomBytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        }
        
        return token;
    }

    private SessionResponse mapToResponse(InterviewSession session) {
        List<TestGenerationResponse> tests = session.getTests().stream()
                .map(test -> {
                    List<QuestionResponse> questions = test.getQuestions().stream()
                            .map(q -> new QuestionResponse(
                                    q.getId(),
                                    q.getText(),
                                    q.getOptionA(),
                                    q.getOptionB(),
                                    q.getOptionC(),
                                    q.getOptionD(),
                                    q.getCorrectAnswer()
                            ))
                            .toList();

                    return new TestGenerationResponse(
                            test.getId(),
                            session.getId(),
                            test.getTopic(),
                            test.getQuestionCount(),
                            test.getCreatedAt(),
                            questions
                    );
                })
                .toList();

        List<AlgorithmTaskInSessionResponse> algorithmTasks = session.getAlgorithmTasks().stream()
                .map(sat -> new AlgorithmTaskInSessionResponse(
                        sat.getAlgorithmTask().getId(),
                        sat.getAlgorithmTask().getTitleRu(),
                        sat.getAlgorithmTask().getDescriptionRu(),
                        sat.getAlgorithmTask().getDifficulty().name(),
                        sat.getAssignedAt()
                ))
                .toList();

        return new SessionResponse(
                session.getId(),
                session.getDescription(),
                session.getCreatedAt(),
                tests,
                algorithmTasks
        );
    }
}

