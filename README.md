# Enterprise Offline RAG Document Processor

## Operations Manual & Deployment Guide

> **Version:** 1.0.0
> **Architecture:** Spring Boot + LangChain4j + Ollama + PostgreSQL + pgvector
> **Deployment Mode:** Fully Offline / Air-Gapped Compatible

---

# Table of Contents

1. Introduction
2. System Architecture
3. Technology Stack
4. Infrastructure Flow
5. Deployment Prerequisites
6. Install Local AI Models
7. Start Infrastructure Services
8. Launch Application
9. Upload and Process Large Documents
10. Verify Vector Storage
11. Query the Knowledge Base
12. Processing Pipeline Internals
13. Performance Optimization
14. Troubleshooting
15. Future Enhancements

---

# 1. Introduction

The Enterprise Offline RAG Document Processor is a fully self-hosted Retrieval-Augmented Generation (RAG) platform designed for organizations that require:

* Complete offline operation
* Zero cloud dependency
* Secure document processing
* AI-powered semantic search
* Local LLM inference
* Large PDF support (1,000+ pages)

The platform ingests enterprise documents, generates vector embeddings, stores them inside PostgreSQL using pgvector, and enables intelligent question answering using locally running Ollama models.

---

# 2. System Architecture

```
                    +--------------------+
                    |    PDF Upload      |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | Spring Boot API    |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | Document Parser    |
                    | (Apache PDFBox)    |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | Text Chunking      |
                    | 1000 Tokens        |
                    | 200 Token Overlap  |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | Embedding Model    |
                    | nomic-embed-text   |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | PostgreSQL         |
                    | pgvector           |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | Similarity Search  |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | llama3.2           |
                    | Local LLM          |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    | AI Generated       |
                    | Response           |
                    +--------------------+
```

---

# 3. Technology Stack

| Component               | Technology       |
| ----------------------- | ---------------- |
| Backend Framework       | Spring Boot 3.x  |
| AI Framework            | LangChain4j      |
| Local LLM Runtime       | Ollama           |
| Embedding Model         | nomic-embed-text |
| Chat Model              | llama3.2         |
| Database                | PostgreSQL       |
| Vector Extension        | pgvector         |
| PDF Processing          | Apache PDFBox    |
| Build Tool              | Maven            |
| Container Runtime       | Docker           |
| Container Orchestration | Docker Compose   |
| Database Administration | pgAdmin          |

---

# 4. Infrastructure Flow

## Step 1: Document Upload

The system accepts PDF files up to 100MB.

```
Client
   |
   v
REST API
```

---

## Step 2: Document Parsing

* Extract raw text
* Preserve page ordering
* Remove unsupported binary elements

```
PDF
 |
 +--> Page 1
 +--> Page 2
 +--> ...
 +--> Page N
```

---

## Step 3: Semantic Chunking

Configuration:

```
Chunk Size    : 1000 Tokens
Chunk Overlap : 200 Tokens
```

Example:

```
Chunk 1:
Tokens 1 - 1000

Chunk 2:
Tokens 801 - 1800

Chunk 3:
Tokens 1601 - 2600
```

The overlap ensures important context is not lost between chunks.

---

## Step 4: Vector Embedding Generation

Each chunk is transformed into a 768-dimensional embedding vector.

```
Text Chunk
     |
     v
nomic-embed-text
     |
     v
[0.234,
 0.882,
 0.112,
 ...
 768 dimensions]
```

---

## Step 5: Vector Storage

Embeddings are stored inside PostgreSQL with pgvector.

Example table:

```sql
document_embeddings
----------------------------------------
id
document_name
chunk_number
content
embedding
created_at
```

---

## Step 6: Similarity Search

When a user asks a question:

```
Question
    |
    v
Generate Embedding
    |
    v
Vector Similarity Search
    |
    v
Top K Results
```

---

## Step 7: Response Generation

Retrieved chunks are passed to llama3.2.

```
Question
      +
Relevant Chunks
      |
      v
Local LLM
      |
      v
Generated Answer
```

---

# 5. Deployment Prerequisites

## Software Requirements

| Software       | Version |
| -------------- | ------- |
| Java           | 21+     |
| Maven          | 3.9+    |
| Docker         | Latest  |
| Docker Compose | Latest  |
| Ollama         | Latest  |
| Git            | Latest  |

---

## Hardware Recommendation

### Minimum

* CPU: 4 Cores
* RAM: 16 GB
* Storage: 100 GB SSD

### Recommended

* CPU: 8+ Cores
* RAM: 32 GB
* Storage: 500 GB NVMe SSD

### Enterprise

* CPU: 16+ Cores
* RAM: 64 GB+
* GPU: Optional
* Storage: 1 TB NVMe

---

# 6. Install Local AI Models

Install Ollama:

```bash
ollama serve
```

Download embedding model:

```bash
ollama pull nomic-embed-text
```

Download reasoning model:

```bash
ollama pull llama3.2
```

Verify:

```bash
ollama list
```

Expected output:

```
NAME                SIZE
nomic-embed-text    xxxx MB
llama3.2            xxxx MB
```

---

# 7. Start Infrastructure Services

Launch PostgreSQL, pgvector, and pgAdmin.

```bash
docker-compose up -d
```

Verify running containers:

```bash
docker ps
```

Expected:

```
postgres
pgadmin
```

---

# 8. Launch Application

Build the application:

```bash
mvn clean package
```

Run:

```bash
java -XX:+UseZGC \
     -jar target/rag-document-processor-1.0.0.jar
```

Expected startup:

```
Started RagDocumentProcessorApplication
Tomcat started on port 8080
Application started successfully
```

---

# 9. Upload and Process Large Documents

Upload a PDF:

```bash
curl -X POST \
     -F "file=@your_massive_document.pdf" \
     http://localhost:8080/api/v1/archive/ingest
```

Example response:

```json
{
  "status": "SUCCESS",
  "document": "your_massive_document.pdf",
  "chunksCreated": 2487,
  "embeddingsGenerated": 2487
}
```

---

# 10. Verify Vector Storage

## Open pgAdmin

```
http://localhost:8082
```

---

## Login

```
Username:
admin@architecture.ai

Password:
admin_password
```

---

## Create Server Connection

| Property | Value       |
| -------- | ----------- |
| Host     | postgres    |
| Port     | 5432        |
| Database | rag_db      |
| Username | senior_arch |
| Password | ********    |

---

## Navigate

```
Servers
   |
   +-- PostgreSQL
          |
          +-- Databases
                  |
                  +-- rag_db
                          |
                          +-- Schemas
                                  |
                                  +-- Tables
                                          |
                                          +-- document_embeddings
```

Inspect:

* Chunk IDs
* Chunk Content
* Embedding Vectors
* Metadata

---

# 11. Query the Knowledge Base

Ask a question:

```bash
curl \
"http://localhost:8080/api/v1/archive/query?prompt=Summarize+the+operational+guidelines+in+section+four"
```

Example response:

```json
{
  "answer": "Section four focuses on deployment and verification...",
  "sources": [
    "Page 221",
    "Page 222",
    "Page 223"
  ]
}
```

---

# 12. Processing Pipeline Internals

```
PDF Upload
      |
      v
PDF Extraction
      |
      v
Chunk Generation
      |
      v
Embedding Generation
      |
      v
Vector Storage
      |
      v
Similarity Search
      |
      v
Context Assembly
      |
      v
LLM Inference
      |
      v
Final Response
```

---

# 13. Performance Optimization

## JVM

```bash
-XX:+UseZGC
-Xms4g
-Xmx8g
```

---

## PostgreSQL

Recommended:

```
shared_buffers = 2GB
work_mem = 64MB
maintenance_work_mem = 512MB
```

---

## Chunk Configuration

```
Chunk Size: 1000
Overlap: 200
Top-K Retrieval: 5
```

---

## Batch Embedding

Process embeddings in batches:

```
Batch Size: 32
```

---

# 14. Troubleshooting

## Ollama Not Running

Check:

```bash
ollama list
```

Start:

```bash
ollama serve
```

---

## Docker Containers Not Available

Check:

```bash
docker ps
```

Restart:

```bash
docker-compose down
docker-compose up -d
```

---

## Database Connection Failure

Verify:

* PostgreSQL container running
* Credentials
* Network configuration
* Port 5432 accessibility

---

## Large PDF Processing Slow

Possible solutions:

* Increase JVM heap
* Increase CPU cores
* Enable parallel embedding generation
* Use NVMe SSD storage

---

## Empty AI Responses

Verify:

* Embeddings generated
* Vector records stored
* Similarity search returns results
* llama3.2 model available

---

# 15. Future Enhancements

Potential enterprise upgrades:

* Multi-document collections
* OCR for scanned PDFs
* Hybrid search (BM25 + Vector)
* Metadata filtering
* Document versioning
* Role-based access control
* Multi-user workspace
* REST API authentication
* Kubernetes deployment
* Distributed vector indexing
* GPU acceleration
* Streaming AI responses
* Incremental document updates
* Monitoring with Prometheus and Grafana

---

# Useful Operational Commands

## Start Ollama

```bash
ollama serve
```

---

## Download Models

```bash
ollama pull nomic-embed-text
ollama pull llama3.2
```

---

## Start Infrastructure

```bash
docker-compose up -d
```

---

## Build Project

```bash
mvn clean package
```

---

## Run Application

```bash
java -XX:+UseZGC \
     -jar target/rag-document-processor-1.0.0.jar
```

---

## Upload Document

```bash
curl -X POST \
     -F "file=@your_massive_document.pdf" \
     http://localhost:8080/api/v1/archive/ingest
```

---

## Query Document

```bash
curl \
"http://localhost:8080/api/v1/archive/query?prompt=Your+Question+Here"
```

---

# Summary

This architecture provides:

* Fully Offline AI
* Enterprise Security
* Large PDF Support
* Local LLM Inference
* Semantic Search
* Vector Database Storage
* Scalable Spring Boot Backend
* Dockerized Infrastructure
* Production-Ready RAG Pipeline

**End of Document**
