package com.contoso.springbootaisearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for RAG implementation with Azure OpenAI and Azure AI Search.
 * This application demonstrates retrieval augmented generation (RAG) using Spring Boot, 
 * Azure OpenAI, and Azure AI Search, deployed to Azure App Service,
 * and secured with passwordless authentication through managed identities.
 */
@SpringBootApplication
public class SpringBootAiSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAiSearchApplication.class, args);
    }
}
