package com.nevzatcirak.assistant.adapter.gemini;

import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.UserQuery;
import com.nevzatcirak.assistant.api.port.LlmPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * Adapter for communicating with the Gemini LLM via Spring AI's ChatClient.
 * This implementation uses an OpenAI-compatible adapter configuration.
 */
@Service
public class GeminiLlmAdapter implements LlmPort {

    private static final Logger logger = LoggerFactory.getLogger(GeminiLlmAdapter.class);
    private final ChatClient chatClient;

    /**
     * Initializes the Gemini LLM Adapter with Chat Memory support.
     *
     * @param builder    Spring AI ChatClient Builder.
     * @param chatMemory Memory storage bean.
     */
    public GeminiLlmAdapter(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultSystem("You are a helpful and professional assistant.") // Translated
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    @Override
    public AssistantResponse generate(String conversationId, String systemPrompt, UserQuery query) {
        logger.debug("Generating response for Conversation ID: {}", conversationId);

        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(query.text())
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .content();

        return new AssistantResponse(response);
    }
}