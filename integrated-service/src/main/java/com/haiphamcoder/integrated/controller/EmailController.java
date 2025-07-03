package com.haiphamcoder.integrated.controller;

import com.haiphamcoder.integrated.domain.model.EmailDetails;
import com.haiphamcoder.integrated.domain.model.EmailTemplate;
import com.haiphamcoder.integrated.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/simple")
    public ResponseEntity<String> sendSimpleEmail(@RequestBody EmailDetails emailDetails) {
        emailService.sendSimpleMessage(emailDetails);
        return ResponseEntity.ok("Email sent to " + emailDetails.getTo());
    }

    @PostMapping("/template")
    public ResponseEntity<String> sendTemplateEmail(@RequestBody EmailDetails emailDetails) {
        emailService.sendMessageWithTemplate(emailDetails, EmailTemplate.OTP);
        return ResponseEntity.ok("Email sent to " + emailDetails.getTo());
    }
} 