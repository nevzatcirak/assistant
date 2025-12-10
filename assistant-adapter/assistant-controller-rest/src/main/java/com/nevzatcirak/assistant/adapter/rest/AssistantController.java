package com.nevzatcirak.assistant.adapter.rest;

import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.UserQuery;
import com.nevzatcirak.assistant.core.usecase.ChatUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class AssistantController {

    private final ChatUseCase chatUseCase;

    public AssistantController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping
    public AssistantResponse chat(@RequestBody UserQuery query) {
        return chatUseCase.chat(query);
    }
}
