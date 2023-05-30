package com.colorfull.order_system.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/word")
    public String getHelloWord() {
        return "hello word !!!";
    }
}
