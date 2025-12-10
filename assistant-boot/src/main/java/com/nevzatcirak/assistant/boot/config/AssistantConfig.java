package com.nevzatcirak.assistant.boot.config;

import com.nevzatcirak.assistant.api.model.PersonProfile;
import com.nevzatcirak.assistant.api.port.*;
import com.nevzatcirak.assistant.core.usecase.ChatUseCase;
import com.nevzatcirak.assistant.core.usecase.DataIngestionUseCase;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {

    @Value("${assistant.person.first-name}")
    private String firstName;

    @Value("${assistant.person.last-name}")
    private String lastName;

    @Value("${assistant.person.role}")
    private String role;

    @Value("${assistant.person.linkedin-url}")
    private String linkedinUrl;

    @Value("${assistant.person.cv-path}")
    private String cvPath;

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public PersonProfile personProfile() {
        return new PersonProfile(firstName, lastName, role, linkedinUrl, cvPath);
    }

    @Bean
    public ChatUseCase chatUseCase(LlmPort llmPort, VectorStorePort vectorStorePort, PersonProfile profile) {
        return new ChatUseCase(llmPort, vectorStorePort, profile);
    }

    @Bean
    public DataIngestionUseCase dataIngestionUseCase(DocumentReaderPort reader, VectorStorePort store) {
        return new DataIngestionUseCase(reader, store);
    }

    @Bean
    public CommandLineRunner init(DataIngestionUseCase ingestionUseCase, PersonProfile profile) {
        return args -> {
            System.out.println("--- Booting Assistant for: " + profile.getFullName() + " ---");
            ingestionUseCase.ingestInitialData(profile);
            System.out.println("--- Initialization Complete ---");
        };
    }
}
