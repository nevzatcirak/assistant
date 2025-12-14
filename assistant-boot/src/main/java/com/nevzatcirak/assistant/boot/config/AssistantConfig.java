package com.nevzatcirak.assistant.boot.config;

import com.nevzatcirak.assistant.api.model.PersonProfile;
import com.nevzatcirak.assistant.api.port.*;
import com.nevzatcirak.assistant.core.usecase.ChatUseCase;
import com.nevzatcirak.assistant.core.usecase.DataIngestionUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot Configuration class for wiring beans and setting up the application context.
 */
@Configuration
public class AssistantConfig {

    private static final Logger logger = LoggerFactory.getLogger(AssistantConfig.class);

    @Value("${assistant.person.first-name}")
    private String firstName;

    @Value("${assistant.person.last-name}")
    private String lastName;

    @Value("${assistant.person.role}")
    private String role;

    @Value("${assistant.person.email}")
    private String email;

    @Value("${assistant.person.phoneNumber}")
    private String phoneNumber;

    @Value("${assistant.person.linkedin-url}")
    private String linkedinUrl;

    @Value("${assistant.person.linkedin-data-path}")
    private String linkedinDataPath;

    @Value("${assistant.person.cv-path}")
    private String cvPath;

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public PersonProfile personProfile() {
        return new PersonProfile(firstName, lastName, role, email, phoneNumber, linkedinUrl, linkedinDataPath, cvPath);
    }

    @Bean
    public ChatUseCase chatUseCase(LlmPort llmPort, VectorStorePort vectorStorePort, PersonProfile profile) {
        return new ChatUseCase(llmPort, vectorStorePort, profile);
    }

    @Bean
    public DataIngestionUseCase dataIngestionUseCase(DocumentReaderPort reader, VectorStorePort store) {
        return new DataIngestionUseCase(reader, store);
    }

    /**
     * Initializes the application data on startup.
     * Ingests CV and LinkedIn data into the in-memory vector store.
     */
    @Bean
    public CommandLineRunner init(DataIngestionUseCase ingestionUseCase, PersonProfile profile) {
        return args -> {
            logger.info("--- Booting NEVA Assistant for: {} ---", profile.getFullName());
            ingestionUseCase.ingestInitialData(profile);
            logger.info("--- Initialization Complete ---");
        };
    }
}