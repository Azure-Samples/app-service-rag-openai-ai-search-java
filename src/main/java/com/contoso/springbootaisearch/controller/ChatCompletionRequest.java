package com.contoso.springbootaisearch.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Request model for chat completion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {
    private List<ClientMessage> messages = new ArrayList<>();
}
