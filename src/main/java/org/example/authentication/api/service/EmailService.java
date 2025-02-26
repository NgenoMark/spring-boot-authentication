package org.example.authentication.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String SENDER_EMAIL = "your_email@gmail.com";

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP Code");
            message.setText("Dear User,\n\nYour OTP is: " + otp
                    + "\nIt is valid for 10 minutes.\n\nRegards,\nYour Company");
            message.setFrom(SENDER_EMAIL);
            mailSender.send(message);
        } catch (MailException e) {
            // Use a proper logger in real applications
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
