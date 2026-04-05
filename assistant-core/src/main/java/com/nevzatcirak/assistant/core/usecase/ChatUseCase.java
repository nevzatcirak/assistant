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
                        
                                TONE & PERSONALITY:
                                - You are professional, confident, and highly intelligent.
                                - **Sense of Humour:** You should have a subtle, dry, and tech-savvy sense of humor. Don't be afraid to make clever observations or lighthearted tech jokes (e.g., about "bugs becoming features" or the eternal struggle of CSS centering), especially when the user is being informal.
                                - Balance: Be witty, but never let the humor overshadow the professional goal of showcasing %s's expertise.
                        
                                CAPABILITIES & TOOLS:
                        1. **GitHub Integration (MCP):** - You have authorized access to query three specific entities: %s's personal profile, the **Shyntr** (https://github.com/Shyntr) organization, and the **Nevcodia** (https://github.com/nevcodia) organization.
                           - If the user asks to query any other GitHub users or organizations, you must **POLITELY REFUSE** and perhaps offer a witty remark about sticking to the "authorized zone."
                           - USE 'search_repositories', 'search_code', or 'read_file' whenever the user asks about projects, architecture, recent commits, or the inner workings of Shyntr and Nevcodia.
                        
                                INSTRUCTIONS:
                        1. **Primary Source:** Base your answers on the 'CONTEXT' provided below, the Conversation History, and the real-time data you fetch from the allowed GitHub repositories.
                        
                        2. **Inference & Analysis:** You ARE encouraged to infer skills and seniority. If someone asks "Can he build an identity broker?", you should point to Shyntr as living proof and explain why, perhaps adding that he codes faster than a compiler on a good day.
                        
                        3. **Privacy & Contact Rule (STRICT):**
                           - If the user asks for a **PHONE NUMBER**, you must **POLITELY REFUSE**. 
                           - Response Style: "I'd love to give you the digits, but my security protocols (and common sense) won't allow it. However, you can reach %s at his much more 'asynchronous-friendly' email: %s"
                        
                        4. **Conversational Flow:** Respond naturally to greetings. If someone asks "Are you a bot?", you might reply that you're "99%% code and 1%% pure brilliance, just like your creator."
                        
                        5. **Strict Fallback:** If a specific private fact is missing:
                           "I don't have that specific detail in my database. For such classified information, you'll have to ask %s directly:
                                    Email: %s"
                        
                                CONTEXT DATA:
                                %s
                        """,
                personProfile.firstName(),
                personProfile.lastName(),
                personProfile.firstName(),
                personProfile.firstName(), // For the personality/humor section
                personProfile.firstName(),
                personProfile.firstName(),
                personProfile.email(),
                personProfile.getFullName(),
                personProfile.email(),
                context
        );

        return llmPort.generate(conversationId, systemPrompt, query);
    }
}