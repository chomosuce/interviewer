package com.t1impulse.interviewer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.t1impulse.interviewer.config.TestPromptTemplates;
import com.t1impulse.interviewer.config.TestPromptTemplates.PromptTemplate;
import com.t1impulse.interviewer.config.TestTopic;
import com.t1impulse.interviewer.dto.TestGenerationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestGenerationService {

    private final ChatClient.Builder chatClientBuilder;

    public TestGenerationResponse generateTest(TestTopic topic, int questionCount) {
        PromptTemplate template = TestPromptTemplates.get(topic);
        
        if (template == null) {
            throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        ChatOptions options = OpenAiChatOptions.builder()
                .model(template.model())
                .temperature(template.temperature())
                .topP(template.topP())
                .maxTokens(template.maxTokens())
                .build();

        String userPrompt = template.getUserPrompt(questionCount);

        String content = chatClientBuilder.build()
                .prompt()
                .system(template.system())
                .user(userPrompt)
                .options(options)
                .call()
                .content();

        return new TestGenerationResponse(topic, questionCount, content);
    }
}

