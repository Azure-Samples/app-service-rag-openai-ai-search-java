package com.contoso.springbootaisearch.controller;

import com.azure.ai.openai.models.ChatCompletions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Response object containing formatted chat response and citations
 * This is the format that our client expects, with a content field and citations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String content;
    private String error;
    @Builder.Default
    private List<Citation> citations = new ArrayList<>();
    
    /**
     * Create a ChatResponse from a ChatCompletions object
     */
    public static ChatResponse fromChatCompletions(ChatCompletions completions) {
        ChatResponse response = new ChatResponse();
        
        if (completions.getChoices() != null && !completions.getChoices().isEmpty()) {
            var message = completions.getChoices().get(0).getMessage();
            if (message != null) {
                response.setContent(message.getContent());

                if (message.getContext() != null && message.getContext().getCitations() != null) {
                    var azureCitations = message.getContext().getCitations();
                    for (int i = 0; i < azureCitations.size(); i++) {
                        var azureCitation = azureCitations.get(i);
                        
                        Citation citation = new Citation();
                        citation.setIndex(i + 1);
                        citation.setTitle(azureCitation.getTitle());
                        citation.setContent(azureCitation.getContent());
                        citation.setFilePath(azureCitation.getFilepath());
                        citation.setUrl(azureCitation.getUrl());
                        
                        response.getCitations().add(citation);
                    }
                }
            }
        }
        
        return response;
    }
    
    /**
     * Individual citation information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Citation {
        private int index;
        private String title;
        private String content;
        private String filePath;
        private String url;
    }
}
