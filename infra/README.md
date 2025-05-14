# Azure infrastructure files

This directory contains the infrastructure as code (Bicep) files needed to deploy the sample application to Azure using Azure Developer CLI (azd).

## Azure resources

The infrastructure includes the following Azure resources:

1. **App Service**: Hosts the web application
2. **Azure OpenAI**: Provides GPT model and embedding model capabilities
3. **Azure AI Search**: Provides vector search capabilities for RAG implementation
4. **Azure Storage Account**: Stores documents and chunks for search index population

All resources are configured with system-assigned managed identities and necessary role assignments to support [Azure OpenAI on Your Data](https://learn.microsoft.com/azure/ai-services/openai/concepts/use-your-data) functionality.