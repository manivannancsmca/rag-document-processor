package com.architecture.ai.controller;

import com.architecture.ai.service.DocumentAssistant;
import com.architecture.ai.service.IngestionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/archive")
@RequiredArgsConstructor
public class ArchitectureControlGateway {

    private final IngestionEngine ingestionEngine;
    private final DocumentAssistant documentAssistant;

    @PostMapping(value = "/ingest", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> loadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "The payload content is empty."));
        }
        
        try {
            // CRITICAL: Read bytes synchronously on the main request thread 
            // before Tomcat can destroy the underlying temp file.
            byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();
            
            // Offload the safe byte data copy to the async executor worker
            ingestionEngine.ingestLargeDocument(fileBytes, filename);
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "status", "Processing",
                    "message", "The document bytes are locked in memory and processing asynchronously."
            ));
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cache document bytes: " + e.getMessage()));
        }
    }

    @GetMapping("/query")
    public ResponseEntity<Map<String, String>> searchAndAsk(@RequestParam("prompt") String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Prompt parameter cannot be blank."));
        }
        String responseContent = documentAssistant.chat(prompt);
        return ResponseEntity.ok(Map.of("reply", responseContent));
    }
}