package com.nevzatcirak.assistant.adapter.vectordb;

import com.nevzatcirak.assistant.api.port.VectorStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * In-Memory Vector Store implementation adapter.
 * Wraps the Spring AI VectorStore interface.
 */
@Service
public class SimpleVectorAdapter implements VectorStorePort {

    private static final Logger logger = LoggerFactory.getLogger(SimpleVectorAdapter.class);
    private final VectorStore vectorStore;

    public SimpleVectorAdapter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void save(List<String> contents) {
        if (contents == null || contents.isEmpty()) {
            logger.warn("Attempted to save empty content list to Vector Store.");
            return;
        }

        List<Document> docs = contents.stream()
                .map(Document::new)
                .toList();
        vectorStore.add(docs);
        logger.info("Saved {} documents to Vector Store.", docs.size());
    }

    @Override
    public List<String> findSimilar(String query, int limit) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(limit)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);
        logger.debug("Vector search found {} results for query: '{}'", results.size(), query);

        return results.stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }
}