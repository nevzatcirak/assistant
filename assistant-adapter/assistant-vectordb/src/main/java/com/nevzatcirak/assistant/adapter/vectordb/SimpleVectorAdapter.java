package com.nevzatcirak.assistant.adapter.vectordb;

import com.nevzatcirak.assistant.api.port.VectorStorePort;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleVectorAdapter implements VectorStorePort {

    private final VectorStore vectorStore;

    public SimpleVectorAdapter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void save(List<String> contents) {
        List<Document> docs = contents.stream()
                .map(Document::new)
                .toList();
        vectorStore.add(docs);
    }

    @Override
    public List<String> findSimilar(String query, int limit) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(limit)
                .build();

        return vectorStore.similaritySearch(request).stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }
}