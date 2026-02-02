package com.nevzatcirak.assistant.adapter.gemini;

import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.UserQuery;
import com.nevzatcirak.assistant.api.port.LlmPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * Adapter for communicating with the Gemini LLM via Spring AI's ChatClient.
 * This implementation uses an OpenAI-compatible adapter configuration.
 */
@Service
public class GeminiLlmAdapter implements LlmPort {
    private static final Logger logger = LoggerFactory.getLogger(GeminiLlmAdapter.class);
    private final ChatClient chatClient;
    private final ToolCallbackProvider tools;

    /**
     * Initializes the Gemini LLM Adapter with Chat Memory and MCP support.
     */
    public GeminiLlmAdapter(ChatClient.Builder builder, ChatMemory chatMemory, ToolCallbackProvider tools) {
        logger.info("Initializing Gemini LLM Adapter...");
        this.tools = tools;
        Arrays.asList(tools.getToolCallbacks()).forEach(tc -> logger.info("Registered Tool: {}", tc.getToolDefinition().name()));
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(tools)
                .build();
    }

    @Override
    public AssistantResponse generate(String conversationId, String systemPrompt, UserQuery query) {
        logger.debug("Generating response for Conversation ID: {}", conversationId);
        try {
            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(query.text())
                    .advisors(a -> a
                            .param(CONVERSATION_ID, conversationId))
                    .call()
                    .content();

            return new AssistantResponse(response);
        } catch (IllegalStateException ex) {
            if (ex.getMessage().contains("No ToolCallback found")) {

                String available = Arrays.stream(tools.getToolCallbacks())
                        .map(tc -> tc.getToolDefinition().name())
                        .sorted()
                        .toList()
                        .toString();

                String retrySystemPrompt = systemPrompt + """
                        IMPORTANT:
                        You MUST ONLY use one of the following tool names exactly as written:
                        """ + available + """
                        
                        Do NOT invent new tool names.
                        If none apply, answer in text without calling a tool.
                        """;

                String retry = chatClient.prompt()
                        .system(retrySystemPrompt)
                        .user(query.text())
                        .advisors(a -> a.param(CONVERSATION_ID, conversationId))
                        .call()
                        .content();

                return new AssistantResponse(retry);
            }
            throw ex;
        }
    }

    @Bean
    ApplicationRunner dumpToolCallbacks(List<ToolCallbackProvider> providers) {
        return args -> {
            System.out.println("=== ToolCallbackProviders: " + providers.size() + " ===");
            for (var p : providers) {
                System.out.println("Provider: " + p.getClass().getName());
                try {
                    ToolCallback[] callbacks = p.getToolCallbacks();
                    System.out.println("  callbacks=" + callbacks.length);
                    for (ToolCallback cb : callbacks) {
                        System.out.println("   - " + cb.getToolDefinition().name());
                    }
                } catch (Exception e) {
                    System.out.println("  ERROR while reading callbacks: " + e.getMessage());
                }
            }
        };
    }
}