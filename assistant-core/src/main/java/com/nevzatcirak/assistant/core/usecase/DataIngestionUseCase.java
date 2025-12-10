package com.nevzatcirak.assistant.core.usecase;

import com.nevzatcirak.assistant.api.model.PersonProfile;
import com.nevzatcirak.assistant.api.port.DocumentReaderPort;
import com.nevzatcirak.assistant.api.port.VectorStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the data ingestion process for the AI Assistant.
 * <p>
 * This use case is responsible for reading data from various sources (CV, LinkedIn),
 * processing them, and storing them into the Vector Database for RAG (Retrieval Augmented Generation).
 * </p>
 */
public class DataIngestionUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DataIngestionUseCase.class);

    private final DocumentReaderPort documentReader;
    private final VectorStorePort vectorStore;

    /**
     * Constructs a new DataIngestionUseCase.
     *
     * @param documentReader Port for reading documents from file system or classpath.
     * @param vectorStore    Port for storing vector embeddings.
     */
    public DataIngestionUseCase(DocumentReaderPort documentReader, VectorStorePort vectorStore) {
        this.documentReader = documentReader;
        this.vectorStore = vectorStore;
    }

    /**
     * Reads initial data from the configured profile paths and ingests them into the Vector Store.
     *
     * @param profile The persona profile containing file paths for CV and LinkedIn data.
     */
    public void ingestInitialData(PersonProfile profile) {
        List<String> allDocs = new ArrayList<>();

        logger.info("Reading CV from path: {}", profile.cvPath());
        List<String> cvContent = documentReader.read(profile.cvPath());
        if (!cvContent.isEmpty()) {
            allDocs.addAll(cvContent);
            logger.debug("Loaded {} segments from CV.", cvContent.size());
        } else {
            logger.warn("CV file was empty or could not be read: {}", profile.cvPath());
        }

        if (profile.linkedinDataPath() != null && !profile.linkedinDataPath().isEmpty()) {
            logger.info("Reading LinkedIn Data from path: {}", profile.linkedinDataPath());
            List<String> linkedinContent = documentReader.read(profile.linkedinDataPath());

            if (!linkedinContent.isEmpty()) {
                allDocs.add("--- LINKEDIN PROFILE DATA START ---");
                allDocs.addAll(linkedinContent);
                allDocs.add("--- LINKEDIN PROFILE DATA END ---");
                logger.debug("Loaded {} segments from LinkedIn data.", linkedinContent.size());
            }
        }

        if (!allDocs.isEmpty()) {
            vectorStore.save(allDocs);
            logger.info("Ingestion Complete. Total documents saved to Vector Store: {}", allDocs.size());
        } else {
            logger.warn("No data found to ingest. Vector Store remains empty.");
        }
    }
}