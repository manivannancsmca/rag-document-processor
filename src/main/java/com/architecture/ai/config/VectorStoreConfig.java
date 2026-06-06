package com.architecture.ai.config;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Value("${infrastructure.datasource.host}")
    private String host;

    @Value("${infrastructure.datasource.port}")
    private Integer port;

    @Value("${infrastructure.datasource.database}")
    private String database;

    @Value("${infrastructure.datasource.user}")
    private String user;

    @Value("${infrastructure.datasource.password}")
    private String password;

    @Value("${infrastructure.datasource.table-name}")
    private String tableName;

    @Value("${infrastructure.datasource.dimension}")
    private Integer dimension;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(host)
                .port(port)
                .user(user)
                .password(password)
                .database(database)
                .table(tableName)
                .dimension(dimension)
                .dropTableFirst(false)
                .build();
    }
}