package com.nevzatcirak.assistant.adapter.rest;

import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.UserQuery;
import com.nevzatcirak.assistant.core.usecase.ChatUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller providing endpoints for the AI Assistant.
 */
@RestController
@RequestMapping("/api/chat")
public class AssistantController {

    private static final Logger logger = LoggerFactory.getLogger(AssistantController.class);
    private final ChatUseCase chatUseCase;

    public AssistantController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    /**
     * Endpoint to chat with the assistant.
     *
     * @param query          The user's query payload.
     * @param conversationId (Optional) Header to track conversation history ("X-Conversation-Id").
     * @return The assistant's response.
     */
    @PostMapping
    public AssistantResponse chat(
            @RequestBody UserQuery query,
            @RequestHeader(value = "X-Conversation-Id", required = false) String conversationId) {

        String chatId = (conversationId != null && !conversationId.isEmpty()) ? conversationId : "default-session";

        logger.info("Incoming chat request. Session: {}", chatId);
        return chatUseCase.chat(chatId, query);
    }
}