package com.contoso.springbootaisearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for web pages using Thymeleaf templates.
 */
@Controller
public class WebController {

    /**
     * Home page (chat interface)
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    /**
     * Error page
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
