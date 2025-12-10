package com.nevzatcirak.assistant.core.usecase;

import com.nevzatcirak.assistant.api.model.*;
import com.nevzatcirak.assistant.api.port.*;
import java.util.List;

public class ChatUseCase {

    private final LlmPort llmPort;
    private final VectorStorePort vectorStorePort;
    private final PersonProfile personProfile;

    public ChatUseCase(LlmPort llmPort, VectorStorePort vectorStorePort, PersonProfile personProfile) {
        this.llmPort = llmPort;
        this.vectorStorePort = vectorStorePort;
        this.personProfile = personProfile;
    }

    public AssistantResponse chat(UserQuery query) {
        // RAG: Find relevant context
        List<String> similarDocs = vectorStorePort.findSimilar(query.text(), 3);
        String context = similarDocs.isEmpty() ? "No specific context available." : String.join("\n---\n", similarDocs);

        // Dynamic System Prompt
        String systemPrompt = String.format("""
            You are the AI Assistant for %s %s, who is a %s.

            INSTRUCTIONS:
            - Answer questions based ONLY on the CONTEXT provided below.
            - If the answer is not in the context, say "I don't have information about that."
            - Be professional and helpful. Refer to the user as "Mr. %s" or "he" when talking about the subject.

            CONTEXT:
            %s
            """,
            personProfile.firstName(),
            personProfile.lastName(),
            personProfile.role(),
            personProfile.lastName(),
            context
        );

        return llmPort.generate(systemPrompt, query);
    }
}
