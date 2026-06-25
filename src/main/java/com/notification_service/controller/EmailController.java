package com.notification_service.controller;

import com.notification_service.dto.EmailRequest;
import com.notification_service.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(
            @Valid @RequestBody EmailRequest request
    ) {

        emailService.sendEmail(request);

        return ResponseEntity.ok("Email sent successfully");
    }
}