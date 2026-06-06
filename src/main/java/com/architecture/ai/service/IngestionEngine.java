package com.architecture.ai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionEngine {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    @Async("ingestionExecutor")
    public CompletableFuture<String> ingestLargeDocument(byte[] fileBytes, String originalFilename) {
        log.info("Beginning background execution pipeline for: {}", originalFilename);
        
        // Wrap the isolated byte array into a fresh stream local to this thread
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            
            // 1. Structural extraction over the byte stream via Apache Tika
            ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
            Document document = parser.parse(inputStream);
            log.info("Document parsing successful. Total characters extracted: {}", document.text().length());

            // 2. Token-based recursive splitting 
            DocumentSplitter splitter = DocumentSplitters.recursive(1000, 200);

            // 3. Ingestion
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(splitter)
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();

            log.info("Generating vectorized embeddings natively via Ollama...");
            ingestor.ingest(document);
            log.info("Vector space ingestion completed successfully.");

            return CompletableFuture.completedFuture("Ingestion successful.");
        } catch (Exception e) {
            log.error("Critical failure during async ingestion pipeline: ", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}