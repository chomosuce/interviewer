package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.TaskResponse;
import com.t1impulse.interviewer.entity.AlgorithmTask;
import com.t1impulse.interviewer.entity.Difficulty;
import com.t1impulse.interviewer.entity.InterviewSession;
import com.t1impulse.interviewer.entity.SessionAlgorithmTask;
import com.t1impulse.interviewer.repository.AlgorithmTaskRepository;
import com.t1impulse.interviewer.repository.SessionAlgorithmTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final AlgorithmTaskRepository algorithmTaskRepository;
    private final SessionAlgorithmTaskRepository sessionAlgorithmTaskRepository;
    private final SessionService sessionService;

    @Transactional
    public TaskResponse getRandomTaskByDifficulty(Difficulty difficulty, UUID sessionId) {
        AlgorithmTask task = algorithmTaskRepository.findRandomByDifficulty(difficulty.name())
                .orElseThrow(() -> new IllegalArgumentException("No tasks found for difficulty: " + difficulty));
        
        assignTaskToSession(task, sessionId);
        return mapToResponse(task, sessionId);
    }

    @Transactional
    public TaskResponse getRandomTask(UUID sessionId) {
        AlgorithmTask task = algorithmTaskRepository.findRandom()
                .orElseThrow(() -> new IllegalArgumentException("No tasks found"));
        
        assignTaskToSession(task, sessionId);
        return mapToResponse(task, sessionId);
    }

    private void assignTaskToSession(AlgorithmTask task, UUID sessionId) {
        if (sessionId != null) {
            InterviewSession session = sessionService.getOrCreateSession(sessionId);
            
            SessionAlgorithmTask sessionTask = SessionAlgorithmTask.builder()
                    .session(session)
                    .algorithmTask(task)
                    .build();
            
            sessionAlgorithmTaskRepository.save(sessionTask);
            log.info("Assigned algorithm task {} to session {}", task.getId(), sessionId);
        }
    }

    private TaskResponse mapToResponse(AlgorithmTask task, UUID sessionId) {
        return new TaskResponse(
                task.getId(),
                task.getTitleRu(),
                task.getDescriptionRu(),
                task.getDifficulty(),
                sessionId
        );
    }
}

