package com.contoso.springbootaisearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for Azure OpenAI and Azure AI Search integration.
 * Maps properties from application.properties to Java fields.
 */
@Configuration
@ConfigurationProperties(prefix = "azure")
@Data
public class AppSettings {
    // Azure OpenAI settings
    private OpenAISettings openai = new OpenAISettings();
    
    // Azure AI Search settings
    private SearchSettings search = new SearchSettings();
    
    @Data
    public static class OpenAISettings {
        private String endpoint;
        private GptSettings gpt = new GptSettings();
        private EmbeddingSettings embedding = new EmbeddingSettings();
        
        @Data
        public static class GptSettings {
            private String deployment;
        }
        
        @Data
        public static class EmbeddingSettings {
            private String deployment;
        }
    }
    
    @Data
    public static class SearchSettings {
        private String url;
        private IndexSettings index = new IndexSettings();
        
        @Data
        public static class IndexSettings {
            private String name;
        }
    }
}
