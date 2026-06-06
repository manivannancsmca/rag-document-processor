# Enterprise Offline RAG Document Processor - Operations Manual

An enterprise-grade, completely offline Retrieval-Augmented Generation (RAG) infrastructure blueprint designed to ingest, slice, index, and query heavy, high-volume files (such as 1,000+ page PDFs) leveraging local LLMs and a containerized vector database.

---

## 🏗️ Architectural Topology & Infrastructure Flow

The system coordinates structural document intelligence completely inside a local deployment boundary:

1. **Ingestion Port:** Accepts binary PDF payloads up to 100MB via secure HTTP channels.
2. **Asynchronous Slicing Worker:** Breaks text blocks into 1,000-token chunks with a 200-token semantic overlap buffer to preserve critical cross-page context.
3. **Vector Vectorization Mapping:** Converts dense text blocks into 768-dimensional mathematical coordinates using a local embedding matrix.
4. **Isolated Vector Storage:** Saves coordinates and text context into a persistent relational database using high-performance similarity indexing.
5. **Contextual Retrieval Loop:** Pulls top matching information blocks via similarity scores and supplies them to a localized reasoning model to build responses.

---

## 🚀 Operationl Deployment & Verification Guide

1. Set Up Local LLM Engines
Run these commands natively inside your host system's terminal to pull down the foundational intelligence models:

```bash
  # Pull the model for high-dimensional vector math calculations
  ollama pull nomic-embed-text

  # Pull the model for prompt reasoning and content summarization
  ollama pull llama3.2

  docker-compose up -d

  # Launch the Core Engine Application
  java -XX:+UseZGC -jar target/rag-document-processor-1.0.0.jar

  # Upload and Ingest a 1,000-Page Archive
  curl -X POST -F "file=@your_massive_document.pdf" http://localhost:8080/api/v1/archive/ingest

---

## Validate Storage Visuals

1. Open your browser and navigate to: http://localhost:8082
2. Login using the administrative keys: admin@architecture.ai / admin_password.
3. Register a new Server connection pointing to the host address postgres (using internal Docker DNS) on port 5432, DB rag_db, user senior_arch.
4. Navigate to the document_embeddings table to visually inspect your text segments alongside their generated floating-point array keys.

---

## Query the Archive Context

```bash
  curl "http://localhost:8080/api/v1/archive/query?prompt=Summarize+the+operational+guidelines+in+section+four"

---

