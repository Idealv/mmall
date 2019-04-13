package com.joe.category.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {
    @GetMapping("/msg")
    String categoryMsg(){
        return "Hello There is category module";
    }
}
