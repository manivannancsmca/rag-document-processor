package com.architecture.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface DocumentAssistant {

    @SystemMessage("""
        You are an expert AI architect. Use the provided context extracted from a 1,000-page blueprint document to answer the user request.
        If the context does not supply enough information to yield an answer, tell the user gracefully that the details are missing.
        Always keep summaries comprehensive, technical, objective, and structured.
        """)
    String chat(@UserMessage String userMessage);
}