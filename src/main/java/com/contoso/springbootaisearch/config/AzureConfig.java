package com.contoso.springbootaisearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;

/**
 * Configuration class for Azure services.
 * Sets up clients for Azure OpenAI and AI Search with managed identity authentication.
 */
@Configuration
public class AzureConfig {

    @Value("${azure.openai.endpoint}")
    private String openAiEndpoint;

    /**
     * Creates a DefaultAzureCredential for authentication.
     * This credential supports managed identities in production and 
     * local development authentication methods when running locally.
     */
    @Bean
    public TokenCredential tokenCredential() {
        return new DefaultAzureCredentialBuilder().build();
    }

    /**
     * Creates an Azure OpenAI client configured with the endpoint and managed identity.
     */
    @Bean
    public OpenAIAsyncClient openAIClient(TokenCredential tokenCredential) {
        return new OpenAIClientBuilder()
                .endpoint(openAiEndpoint)
                .credential(tokenCredential)
                .buildAsyncClient();
    }
}
