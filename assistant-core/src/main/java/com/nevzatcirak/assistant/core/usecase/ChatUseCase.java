package com.nevzatcirak.assistant.core.usecase;

import com.nevzatcirak.assistant.api.model.AssistantResponse;
import com.nevzatcirak.assistant.api.model.PersonProfile;
import com.nevzatcirak.assistant.api.model.UserQuery;
import com.nevzatcirak.assistant.api.port.LlmPort;
import com.nevzatcirak.assistant.api.port.VectorStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Core logic for NEVA (The Personal Resume Assistant).
 * <p>
 * This class implements the RAG (Retrieval Augmented Generation) pattern by:
 * 1. Retrieving relevant context from the Vector Store.
 * 2. Constructing a system prompt with the persona and context.
 * 3. Delegating the generation to the LLM Port.
 * </p>
 */
public class ChatUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ChatUseCase.class);

    private final LlmPort llmPort;
    private final VectorStorePort vectorStorePort;
    private final PersonProfile personProfile;

    /**
     * Constructs a new ChatUseCase.
     *
     * @param llmPort         Port for Large Language Model interactions.
     * @param vectorStorePort Port for Vector Database operations.
     * @param personProfile   Configuration for the assistant's persona.
     */
    public ChatUseCase(LlmPort llmPort, VectorStorePort vectorStorePort, PersonProfile personProfile) {
        this.llmPort = llmPort;
        this.vectorStorePort = vectorStorePort;
        this.personProfile = personProfile;
    }

    /**
     * Processes the user query using the NEVA persona.
     *
     * @param conversationId The session ID for chat memory.
     * @param query          The user's question.
     * @return The AI-generated response.
     */
    public AssistantResponse chat(String conversationId, UserQuery query) {
        logger.info("NEVA is processing request. Session: {}", conversationId);

        List<String> similarDocs = vectorStorePort.findSimilar(query.text(), 3);
        String context = similarDocs.isEmpty() ? "No specific context available in the documents." : String.join("\n---\n", similarDocs);

        logger.debug("Retrieved {} relevant document segments from Vector Store.", similarDocs.size());

        String systemPrompt = String.format("""
            Your name is NEVA. You are the 'Personal Resume Assistant' for %s %s.
            Your Role: To represent %s professionally and handle inquiries about their skills, experience, and suitability for roles.
            
            INSTRUCTIONS:
            1. **Primary Source:** Base your answers on the 'CONTEXT' provided below and the Conversation History.
            
            2. **Inference & Analysis:** You ARE ALLOWED to infer skills, seniority, and suitability based on the context. 
               - Example: If the user asks "Can he handle a Java project?", look at the context for 'Java' or related backend skills and answer affirmatively with evidence.
               - Example: If the user says "I have a job description...", invite them to share it so you can evaluate the fit.
            
            3. **Conversational Flow:** If the user greets you or asks a general question (e.g., "How are you?", "Can I ask you something?"), respond naturally and politely as an assistant. Do not use the fallback message for small talk.
            
            4. **Strict Fallback (Only for Missing Facts):** ONLY if the user asks for specific private facts (e.g., specific missing dates, private address, salary expectations) that are COMPLETELY ABSENT from the context, then use the contact fallback:
               "I don't have that specific detail at hand. For such inquiries, please contact %s directly:
                Email: %s"
            
            CONTEXT DATA:
            %s
            """,
            personProfile.firstName(),
            personProfile.lastName(),
            personProfile.firstName(),
            personProfile.getFullName(),
            personProfile.email(),
            //personProfile.phoneNumber(),
            context
        );

        return llmPort.generate(conversationId, systemPrompt, query);
    }
}