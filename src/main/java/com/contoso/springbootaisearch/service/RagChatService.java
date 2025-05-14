package com.contoso.springbootaisearch.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.*;
import com.contoso.springbootaisearch.config.AppSettings;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that provides Retrieval Augmented Generation (RAG) capabilities
 * by connecting Azure OpenAI with Azure AI Search for grounded responses.
 */
@Service
@Slf4j
public class RagChatService {

    private final AppSettings appSettings;
    private final OpenAIAsyncClient asyncClient;
    
    @Value("${system.prompt}")
    private String systemPrompt;

    public RagChatService(AppSettings appSettings, OpenAIAsyncClient openAIClient) {
        this.appSettings = appSettings;
        this.asyncClient = openAIClient;
        
        // Validate required settings
        validateSettings();
        
        log.info("RagChatService initialized with settings");
    }
    
    private void validateSettings() {
        if (appSettings.getOpenai().getEndpoint() == null) {
            throw new IllegalArgumentException("OpenAI endpoint must be configured");
        }
        if (appSettings.getOpenai().getGpt().getDeployment() == null) {
            throw new IllegalArgumentException("OpenAI GPT deployment must be configured");
        }
        if (appSettings.getSearch().getUrl() == null) {
            throw new IllegalArgumentException("Search service URL must be configured");
        }
        if (appSettings.getSearch().getIndex().getName() == null) {
            throw new IllegalArgumentException("Search index name must be configured");
        }
    }

    /**
     * Processes a chat completion request with RAG capabilities by integrating with Azure AI Search.
     *
     * @param history The chat history containing previous messages
     * @return A Mono containing the AI-generated content and any relevant citations
     */
    public Mono<ChatCompletions> getChatCompletion(List<ChatRequestMessage> history) {
        try {
            // Limit chat history to the 20 most recent messages to prevent token limit issues
            List<ChatRequestMessage> recentHistory = history.size() <= 20 
                ? history 
                : history.subList(history.size() - 20, history.size());
                
            // Add system message to provide context and instructions to the model
            List<ChatRequestMessage> azureMessages = new ArrayList<>();
            
            // Add system message
            azureMessages.add(new ChatRequestSystemMessage(systemPrompt));
            
            // Add the user's messages to the request
            azureMessages.addAll(recentHistory);
            
            AzureSearchChatExtensionConfiguration searchConfiguration =
                new AzureSearchChatExtensionConfiguration(
                        new AzureSearchChatExtensionParameters(appSettings.getSearch().getUrl(), appSettings.getSearch().getIndex().getName())
                                .setAuthentication(new OnYourDataSystemAssignedManagedIdentityAuthenticationOptions())
                                .setQueryType(AzureSearchQueryType.VECTOR_SEMANTIC_HYBRID)
                                .setInScope(true)
                                .setTopNDocuments(2)
                                // the deployment name of the embedding model when you are using a vector or hybrid query type
                                .setEmbeddingDependency(new OnYourDataDeploymentNameVectorizationSource(appSettings.getOpenai().getEmbedding().getDeployment()))
                                .setSemanticConfiguration(appSettings.getSearch().getIndex().getName() + "-semantic-configuration")
                );
            
            // Configure chat completion options
            ChatCompletionsOptions options = new ChatCompletionsOptions(azureMessages)
                .setDataSources(List.of(searchConfiguration));
            
            // Call Azure OpenAI for completion
            return asyncClient.getChatCompletions(
                appSettings.getOpenai().getGpt().getDeployment(), 
                options);
                
        } catch (Exception ex) {
            log.error("Error in getChatCompletion", ex);
            return Mono.error(ex);
        }
    }
}
