package com.just.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/")
    public String index() {
        return say();
    }

    public String say() {
        String word = "hello";
        System.out.println(word);
        return word;
    }
}
