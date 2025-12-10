package com.nevzatcirak.assistant.adapter.gemini;

import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.UserQuery;
import com.nevzatcirak.assistant.api.port.LlmPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiLlmAdapter implements LlmPort {

    private final ChatClient chatClient;

    public GeminiLlmAdapter(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("Sen yardımsever bir asistansın.")
                .build();
    }

    @Override
    public AssistantResponse generate(String systemPrompt, UserQuery query) {
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(query.text())
                .call()
                .content();
        return new AssistantResponse(response);
    }
}
