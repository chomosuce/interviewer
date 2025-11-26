package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.CreateSessionRequest;
import com.t1impulse.interviewer.dto.SessionResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse.QuestionResponse;
import com.t1impulse.interviewer.entity.InterviewSession;
import com.t1impulse.interviewer.repository.InterviewSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final InterviewSessionRepository sessionRepository;

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

    public InterviewSession getOrCreateSession(UUID sessionId) {
        if (sessionId != null) {
            return sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        }
        // Создаём новую сессию если не передан ID
        InterviewSession session = InterviewSession.builder().build();
        return sessionRepository.save(session);
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

        return new SessionResponse(
                session.getId(),
                session.getDescription(),
                session.getCreatedAt(),
                tests
        );
    }
}

