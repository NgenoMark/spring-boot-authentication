package org.example.authentication.api.controller;


import org.example.authentication.api.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.example.authentication.api.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private UserService userService;

    // Resend OTP for Registration Verification
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email) {
        return userService.resendOtp(email);
    }

    // Send OTP for Password Recovery
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return userService.sendPasswordResetOtp(email);
    }
}
