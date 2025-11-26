package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.TaskResponse;
import com.t1impulse.interviewer.entity.AlgorithmTask;
import com.t1impulse.interviewer.entity.Difficulty;
import com.t1impulse.interviewer.repository.AlgorithmTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final AlgorithmTaskRepository algorithmTaskRepository;

    public TaskResponse getRandomTaskByDifficulty(Difficulty difficulty) {
        AlgorithmTask task = algorithmTaskRepository.findRandomByDifficulty(difficulty.name())
                .orElseThrow(() -> new IllegalArgumentException("No tasks found for difficulty: " + difficulty));
        
        return mapToResponse(task);
    }

    public TaskResponse getRandomTask() {
        AlgorithmTask task = algorithmTaskRepository.findRandom()
                .orElseThrow(() -> new IllegalArgumentException("No tasks found"));
        
        return mapToResponse(task);
    }

    private TaskResponse mapToResponse(AlgorithmTask task) {
        return new TaskResponse(
                task.getId(),
                task.getTitleRu(),
                task.getDescriptionRu(),
                task.getDifficulty()
        );
    }
}

