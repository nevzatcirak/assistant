package com.nevzatcirak.assistant.adapter.reader;

import com.nevzatcirak.assistant.api.port.DocumentReaderPort;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TikaReaderAdapter implements DocumentReaderPort {

    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public List<String> read(String sourcePath) {
        try {
            var resource = resourceLoader.getResource(sourcePath);
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            var docs = reader.read();

            // Split huge texts into chunks
            TokenTextSplitter splitter = new TokenTextSplitter();
            return splitter.apply(docs).stream()
                    .map(org.springframework.ai.document.Document::getText)
                    .toList();
        } catch (Exception e) {
            System.err.println("Failed to read document: " + sourcePath + " Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}