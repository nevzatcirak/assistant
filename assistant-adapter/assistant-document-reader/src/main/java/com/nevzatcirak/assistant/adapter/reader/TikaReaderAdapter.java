package com.nevzatcirak.assistant.adapter.reader;

import com.nevzatcirak.assistant.api.port.DocumentReaderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of DocumentReaderPort using Apache Tika.
 * Capable of reading various file formats (PDF, TXT, DOCX, etc.) via Spring AI.
 */
@Service
public class TikaReaderAdapter implements DocumentReaderPort {

    private static final Logger logger = LoggerFactory.getLogger(TikaReaderAdapter.class);
    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    /**
     * Reads a document from the specified path and splits it into tokens.
     *
     * @param sourcePath The file path (e.g., classpath:documents/cv.txt).
     * @return A list of text chunks derived from the document.
     */
    @Override
    public List<String> read(String sourcePath) {
        try {
            logger.debug("Attempting to read document from: {}", sourcePath);
            var resource = resourceLoader.getResource(sourcePath);
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            var docs = reader.read();

            TokenTextSplitter splitter = new TokenTextSplitter();
            return splitter.apply(docs).stream()
                    .map(org.springframework.ai.document.Document::getText)
                    .toList();
        } catch (Exception e) {
            logger.error("Failed to read document from path: {}. Error: {}", sourcePath, e.getMessage());
            return Collections.emptyList();
        }
    }
}