package com.t1impulse.interviewer.service;

import com.t1impulse.interviewer.dto.CodeReviewRequest;
import com.t1impulse.interviewer.dto.CodeReviewResponse;
import com.t1impulse.interviewer.entity.AlgorithmTask;
import com.t1impulse.interviewer.repository.AlgorithmTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeReviewService {

    private final ChatClient.Builder chatClientBuilder;
    private final AlgorithmTaskRepository algorithmTaskRepository;

    private static final String SYSTEM_PROMPT = """
            Ты — строгий и лаконичный ревьюер решений алгоритмических задач в стиле LeetCode. Тебе дают формулировку задачи и код решения на Python. Твоя задача — найти потенциальные логические ошибки в решении.
            
            Требования к ответу:
            1) Не предлагай исправленный код и не переписывай решение.
            2) Укажи только строку или диапазон строк, где, по твоему мнению, есть логическая ошибка или опасное место.
            3) Формат ответа строго один: либо «Строка X», либо «Строка X: короткий комментарий», либо «Строки X–Y: короткий комментарий».
            4) Строки нумеруются, начиная с 1 от строки `class Solution:` (эта строка — строка 1).
            5) Не давай развёрнутых объяснений, не пиши ничего кроме одной строки указанного формата.
            """;

    public CodeReviewResponse reviewCode(CodeReviewRequest request) {
        // Получаем задачу из базы данных
        AlgorithmTask task = algorithmTaskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + request.taskId()));

        // Формируем промпт пользователя
        String userPrompt = String.format(
                "Задача %d: %s\n%s\n\nВот код решения на Python:\n\n```python\n%s\n```\n\nНайди потенциальную логическую ошибку в этом решении и укажи, на какую строку или строки нужно обратить внимание. Ответ дай строго в формате из системных инструкций.",
                task.getId(),
                task.getTitleRu(),
                task.getDescriptionRu(),
                request.codeSnippet()
        );

        // Настраиваем опции для AI
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("qwen3-32b-awq")
                .temperature(0.1)
                .topP(0.9)
                .maxTokens(100)
                .build();

        log.info("Reviewing code for task {}: {}", task.getId(), task.getTitleRu());

        String reviewResult;
        try {
            reviewResult = chatClientBuilder.build()
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userPrompt)
                    .options(options)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Failed to review code via AI API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to review code: " + e.getMessage() + ". Check API key and server availability.", e);
        }

        log.debug("AI review response: {}", reviewResult);

        // Очищаем ответ от возможных markdown блоков
        reviewResult = cleanReviewResponse(reviewResult);

        return new CodeReviewResponse(reviewResult);
    }

    private String cleanReviewResponse(String response) {
        String cleaned = response.trim();
        
        // Удаляем markdown блоки кода если есть
        if (cleaned.contains("```")) {
            int start = cleaned.indexOf("```");
            int end = cleaned.lastIndexOf("```");
            if (end > start) {
                cleaned = cleaned.substring(start + 3, end).trim();
            } else {
                cleaned = cleaned.replace("```", "").trim();
            }
        }
        
        // Берем только первую строку (согласно требованиям)
        int newlineIndex = cleaned.indexOf('\n');
        if (newlineIndex > 0) {
            cleaned = cleaned.substring(0, newlineIndex).trim();
        }
        
        return cleaned;
    }
}

