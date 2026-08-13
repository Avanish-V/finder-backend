package com.iotabuild.campuscircle

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/test")
class TestController{

    @ResponseBody
    @GetMapping("/get")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello Finder - Social Networking")
    }
}