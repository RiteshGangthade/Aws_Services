package com.example.awsdemo.controllers;

import com.example.awsdemo.service.SqsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sqs")
public class SqsController {

    private final SqsService sqsService;

    public SqsController(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestParam String message) {
        sqsService.sendMessage(message);
        return ResponseEntity.ok("Message sent to SQS");
    }
}
