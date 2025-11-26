package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.RunRequest;
import com.t1impulse.interviewer.dto.RunResponse;
import com.t1impulse.interviewer.dto.SubmitSolutionRequest;
import com.t1impulse.interviewer.dto.TestDto;
import com.t1impulse.interviewer.entity.TaskTest;
import com.t1impulse.interviewer.repository.AlgorithmTaskRepository;
import com.t1impulse.interviewer.repository.TaskTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {

    private final AlgorithmTaskRepository algorithmTaskRepository;
    private final TaskTestRepository taskTestRepository;
    private final CodeRunnerService codeRunnerService;

    @Transactional(readOnly = true)
    public RunResponse submitSolution(SubmitSolutionRequest request) {
        // Проверяем существование задачи
        if (!algorithmTaskRepository.existsById(request.taskId())) {
            throw new IllegalArgumentException("Task not found: " + request.taskId());
        }

        // Получаем все тесты для задачи
        List<TaskTest> taskTests = taskTestRepository.findByTaskId(request.taskId());
        
        if (taskTests.isEmpty()) {
            throw new IllegalArgumentException("No tests found for task: " + request.taskId());
        }

        // Преобразуем тесты в формат для CodeRunnerService
        List<TestDto> tests = taskTests.stream()
                .map(tt -> new TestDto(tt.getTestInput(), tt.getTestOutput()))
                .toList();

        // Создаем запрос для запуска кода
        RunRequest runRequest = new RunRequest(
                request.language(),
                request.source(),
                tests,
                5000, // 5 секунд лимит времени по умолчанию
                256   // 256 MB лимит памяти по умолчанию
        );

        // Запускаем код с тестами
        RunResponse response = codeRunnerService.run(runRequest);
        
        log.info("Solution submitted for task {}: status={}, passed={}/{}", 
                request.taskId(), 
                response.status(),
                response.results().stream().filter(r -> "OK".equals(r.status())).count(),
                response.results().size());

        return response;
    }
}

