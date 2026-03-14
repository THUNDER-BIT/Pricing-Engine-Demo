package com.pricing_engine.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // This tells Spring to look for an HTML template
public class BillingUIController {

    @GetMapping("/")
    public String index() {
        // This returns the name of the file in src/main/resources/templates/
        // It should match "index.html" exactly
        return "index";
    }
}