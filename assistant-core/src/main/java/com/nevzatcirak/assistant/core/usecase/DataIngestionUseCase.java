package com.nevzatcirak.assistant.core.usecase;

import com.nevzatcirak.assistant.api.model.PersonProfile;
import com.nevzatcirak.assistant.api.port.DocumentReaderPort;
import com.nevzatcirak.assistant.api.port.VectorStorePort;
import java.util.List;

public class DataIngestionUseCase {

    private final DocumentReaderPort documentReader;
    private final VectorStorePort vectorStore;

    public DataIngestionUseCase(DocumentReaderPort documentReader, VectorStorePort vectorStore) {
        this.documentReader = documentReader;
        this.vectorStore = vectorStore;
    }

    public void ingestInitialData(PersonProfile profile) {
        // Read CV
        List<String> content = documentReader.read(profile.cvPath());
        if (!content.isEmpty()) {
            vectorStore.save(content);
        }
        // Future: Add LinkedIn scraping logic here
    }
}
