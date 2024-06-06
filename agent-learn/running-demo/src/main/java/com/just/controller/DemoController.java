package com.just.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    private final String flag = "flag{thisisflag}";
    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }
}
