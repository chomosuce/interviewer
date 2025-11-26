package com.t1impulse.interviewer.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import com.t1impulse.interviewer.service.ChatService;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public String chat(@RequestBody String question,
            HttpServletRequest request) {

        String convId = request.getHeader("X-Conv-Id");
        if (convId == null || convId.isBlank()) {
            convId = UUID.randomUUID().toString();
        }

        return chatService.ask(convId, question);
    }
}
