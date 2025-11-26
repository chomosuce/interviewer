package com.t1impulse.interviewer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.t1impulse.interviewer.config.TestPromptTemplates;
import com.t1impulse.interviewer.config.TestPromptTemplates.PromptTemplate;
import com.t1impulse.interviewer.config.TestTopic;
import com.t1impulse.interviewer.dto.AiTestResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse;
import com.t1impulse.interviewer.dto.TestGenerationResponse.QuestionResponse;
import com.t1impulse.interviewer.entity.GeneratedTest;
import com.t1impulse.interviewer.entity.InterviewSession;
import com.t1impulse.interviewer.entity.Question;
import com.t1impulse.interviewer.repository.GeneratedTestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestGenerationService {

    private final ChatClient.Builder chatClientBuilder;
    private final GeneratedTestRepository testRepository;
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    @Transactional
    public TestGenerationResponse generateTest(TestTopic topic, int questionCount, UUID sessionId) {
        PromptTemplate template = TestPromptTemplates.get(topic);
        
        if (template == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        // Получаем или создаём сессию
        InterviewSession session = sessionService.getOrCreateSession(sessionId);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(template.model())
                .temperature(template.temperature())
                .topP(template.topP())
                .maxTokens(template.maxTokens())
                .build();

        String userPrompt = template.getUserPrompt(questionCount);

        log.info("Generating test for topic: {}, questions: {}, session: {}", topic, questionCount, session.getId());

        String content = chatClientBuilder.build()
                .prompt()
                .system(template.system())
                .user(userPrompt)
                .options(options)
                .call()
                .content();

        log.debug("AI response: {}", content);

        // Парсим JSON ответ
        AiTestResponse aiResponse = parseAiResponse(content);

        // Сохраняем в БД
        GeneratedTest test = GeneratedTest.builder()
                .topic(topic)
                .questionCount(aiResponse.questions().size())
                .session(session)
                .build();

        for (AiTestResponse.QuestionDto questionDto : aiResponse.questions()) {
            Question question = Question.builder()
                    .text(questionDto.text())
                    .optionA(questionDto.options().get("a"))
                    .optionB(questionDto.options().get("b"))
                    .optionC(questionDto.options().get("c"))
                    .optionD(questionDto.options().get("d"))
                    .correctAnswer(questionDto.correctAnswer().toLowerCase())
                    .build();
            test.addQuestion(question);
        }

        GeneratedTest savedTest = testRepository.save(test);
        log.info("Test saved with id: {}, session: {}", savedTest.getId(), session.getId());

        return mapToResponse(savedTest);
    }

    public TestGenerationResponse getTestById(Long testId) {
        GeneratedTest test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));
        return mapToResponse(test);
    }

    public List<TestGenerationResponse> getTestsByTopic(TestTopic topic) {
        return testRepository.findByTopicOrderByCreatedAtDesc(topic)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AiTestResponse parseAiResponse(String content) {
        try {
            String json = content.trim();
            
            // Удаляем <think>...</think> блоки (модель может их добавлять)
            json = json.replaceAll("(?s)<think>.*?</think>", "").trim();
            
            // Извлекаем JSON из markdown блока если есть
            if (json.contains("```json")) {
                int start = json.indexOf("```json") + 7;
                int end = json.indexOf("```", start);
                if (end > start) {
                    json = json.substring(start, end).trim();
                }
            } else if (json.contains("```")) {
                int start = json.indexOf("```") + 3;
                int end = json.indexOf("```", start);
                if (end > start) {
                    json = json.substring(start, end).trim();
                }
            }
            
            // Если JSON всё ещё обёрнут в backticks
            if (json.startsWith("```")) {
                json = json.substring(3);
            }
            if (json.endsWith("```")) {
                json = json.substring(0, json.length() - 3);
            }
            json = json.trim();

            return objectMapper.readValue(json, AiTestResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", content, e);
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }

    private TestGenerationResponse mapToResponse(GeneratedTest test) {
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

        UUID sessionId = test.getSession() != null ? test.getSession().getId() : null;

        return new TestGenerationResponse(
                test.getId(),
                sessionId,
                test.getTopic(),
                test.getQuestionCount(),
                test.getCreatedAt(),
                questions
        );
    }
}
