<p align="center">
  <img src="docs/logo.svg" alt="Project Logo" width="175">
  <br>
  <i>NEVA - AI Personal Resume Assistant</i>
</p>

**NEVA (NEV Virtual Assistant)** is an AI-powered personal assistant designed to represent a candidate professionally. Built with **Spring AI** and **Hexagonal Architecture**, NEVA uses RAG (Retrieval Augmented Generation) to answer questions about the candidate's skills, experience, and background based on their CV and LinkedIn profile.

## 🚀 Key Features

* **Hybrid AI Architecture:** Uses **Google Gemini 2.0 Flash** for chat generation and **Gemini Embeddings Models** for vector embeddings to ensure zero-cost operation on the Free Tier.
* **RAG (Retrieval Augmented Generation):** Ingests CV (PDF/TXT) and LinkedIn data, converting them into vector embeddings for context-aware answers.
* **Conversational Memory:** Remembers the context of the chat session, allowing follow-up questions (e.g., "What was his role there?").
* **Smart Persona (NEVA):** Configured with a specific system prompt to act as a professional representative, capable of inferring skills and handling missing information gracefully.
* **Hexagonal Architecture:** Strictly decoupled domain logic (`assistant-core`) from infrastructure (`assistant-adapter`).

## 🛠 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 4.0.1
* **AI Framework:** Spring AI 2.0.0-M2
* **LLM:** Google Gemini 2.0 Flash
* **Embedding Model:** `gemini-embedding-001` (Local Transformer, runs on CPU)
* **MCPs:** Github MCP Server
* **Vector Store:** SimpleVectorStore (In-Memory)
* **Document Reader:** Apache Tika
* **Build Tool:** Maven

## 📂 Project Structure (Hexagonal)

The project is divided into multi-modules to enforce architectural boundaries:

* **`assistant-api`**: Domain models (`PersonProfile`, `UserQuery`) and Interface Ports (`LlmPort`, `VectorStorePort`).
* **`assistant-core`**: Pure business logic (`ChatUseCase`, `DataIngestionUseCase`). No framework dependencies.
* **`assistant-adapter`**: Infrastructure implementations:
    * *AI:* Gemini LLM Adapter.
    * *VectorDB:* Simple In-Memory Store Adapter.
    * *Reader:* Tika Document Reader.
    * *Controller:* REST API endpoints.
* **`assistant-boot`**: Application configuration, Bean wiring, and `main` entry point.

## ⚙️ Configuration

The application requires a **Google Gemini API Key**. 

You must configure your API key and personal details in the `application.properties` file located at `assistant-boot/src/main/resources/application.properties`.

```properties
spring.ai.google.genai.api-key=AIzaSyYourKeyHere...
spring.ai.google.genai.project-id=gen-lang-client...
spring.ai.google.genai.embedding.api-key=AIzaSyYourKeyHere...
spring.ai.google.genai.embedding.project-id=gen-lang-client...
spring.ai.google.genai.chat.options.model=gemini-2.0-flash

# Persona Configuration
assistant.person.first-name=Nevzat
assistant.person.last-name=Cirak
assistant.person.role=Senior Software Engineer
assistant.person.email=contact@example.com
assistant.person.phone=+90 555 000 00 00
assistant.person.linkedin-url=[https://linkedin.com/in/nevzatcirak](https://linkedin.com/in/nevzatcirak)
assistant.person.cv-path=classpath:documents/cv.txt
assistant.person.linkedin-data-path=classpath:documents/linkedin_profile.txt