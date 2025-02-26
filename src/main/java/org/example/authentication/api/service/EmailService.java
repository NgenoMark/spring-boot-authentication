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

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP Code");
            message.setText("Dear User,\n\nYour OTP is: " + otp
                    + "\nIt is valid for 10 minutes.\n\nRegards,\nYour Company");
            message.setFrom("your_email@gmail.com");
            mailSender.send(message);
        } catch (MailException e) {
            // Log the error (Use a logger in a real-world application)
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
