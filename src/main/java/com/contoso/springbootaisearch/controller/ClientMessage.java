package com.contoso.springbootaisearch.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple message format sent by client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientMessage {
    private String role;
    private String content;
}
