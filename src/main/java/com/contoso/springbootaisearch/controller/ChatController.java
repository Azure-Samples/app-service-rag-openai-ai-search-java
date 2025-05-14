package com.contoso.springbootaisearch.controller;

import com.azure.ai.openai.models.*;
import com.contoso.springbootaisearch.service.RagChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for handling chat requests.
 * Provides endpoints for chat completion with RAG capabilities.
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final RagChatService ragChatService;

    public ChatController(RagChatService ragChatService) {
        this.ragChatService = ragChatService;
    }

    /**
     * Processes a chat completion request with RAG capabilities.
     * 
     * @param request The chat request containing message history
     * @return A Mono with AI-generated content and citations in a format the client expects
     */
    @PostMapping("/completion")
    public Mono<ChatResponse> getChatCompletion(@RequestBody ChatCompletionRequest request) {
        log.debug("Received chat completion request with {} messages", 
            request.getMessages() != null ? request.getMessages().size() : 0);
        
        // Validate that the request has messages
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            log.error("Request messages are null or empty");
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setError("Chat messages cannot be null or empty");
            return Mono.just(errorResponse);
        }
        
        // Convert messages to Azure OpenAI format
        List<ChatRequestMessage> azureMessages = new ArrayList<>();
            
        for (ClientMessage clientMsg : request.getMessages()) {
            if (clientMsg.getContent() == null || clientMsg.getContent().trim().isEmpty()) {
                continue;
            }
                
            if ("user".equals(clientMsg.getRole())) {
                azureMessages.add(new ChatRequestUserMessage(clientMsg.getContent()));
            } else if ("assistant".equals(clientMsg.getRole())) {
                azureMessages.add(new ChatRequestAssistantMessage(clientMsg.getContent()));
            } else if ("system".equals(clientMsg.getRole())) {
                azureMessages.add(new ChatRequestSystemMessage(clientMsg.getContent()));
            }
        }
            
        if (azureMessages.isEmpty()) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setError("No valid messages to process");
            return Mono.just(errorResponse);
        }
            
            // Convert Azure OpenAI response to our ChatResponse format
            return ragChatService.getChatCompletion(azureMessages)
                .map(completions -> {
                    log.debug("Received response from Azure OpenAI");
                    ChatResponse response = ChatResponse.fromChatCompletions(completions);
                    return response;
                })
                .onErrorResume(ex -> {
                    log.error("Error processing chat completion", ex);
                    
                    // Create error response
                    ChatResponse errorResponse = new ChatResponse();
                    String errorMessage = ex.getMessage();

                    // Check for rate limit errors
                    if (errorMessage != null && (errorMessage.contains("429") || errorMessage.contains("Rate limit"))) {
                        errorResponse.setError("The AI service is currently experiencing high demand. Please wait a moment and try again.");
                    } else {
                        errorResponse.setError("Error processing request");
                    }
                    
                    return Mono.just(errorResponse);
                });
    }
}
