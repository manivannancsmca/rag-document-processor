package com.architecture.ai.config;

import com.architecture.ai.service.DocumentAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {

    @Bean
    public DocumentAssistant documentAssistant(
            ChatLanguageModel chatLanguageModel,
            EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore) {

        // Build content retriever to extract top 5 relevant text snippets from PGVector
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.65) // Filter out noise
                .build();

        return AiServices.builder(DocumentAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();
    }
}