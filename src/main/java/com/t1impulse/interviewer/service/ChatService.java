package com.t1impulse.interviewer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public String ask(String conversationId, String question) {
        return chatClient
                .prompt()
                .user(question)
                .advisors(spec -> spec.param(
                        "chat_memory_conversation_id",
                        conversationId
                ))
                .call()
                .content();
    }
}
