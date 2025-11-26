package com.t1impulse.interviewer.controller;

import com.t1impulse.interviewer.dto.TaskResponse;
import com.t1impulse.interviewer.entity.Difficulty;
import com.t1impulse.interviewer.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/random")
    public ResponseEntity<TaskResponse> getRandomTask(
            @RequestParam(required = false) Difficulty difficulty
    ) {
        try {
            TaskResponse task;
            if (difficulty != null) {
                task = taskService.getRandomTaskByDifficulty(difficulty);
            } else {
                task = taskService.getRandomTask();
            }
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

