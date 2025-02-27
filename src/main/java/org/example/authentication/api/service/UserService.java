package org.example.authentication.api.service;

import org.example.authentication.api.model.User;
import org.example.authentication.api.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 3;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String registerUser(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return "User already registered!";
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Hashing the password

        String otp = generateOtp();
        newUser.setOtp(otp);
        newUser.setOtpGeneratedTime(Instant.now());
        newUser.setOtpAttempts(0);
        userRepository.save(newUser);

        // Send OTP Email
        emailService.sendOtpEmail(email, otp);
        return "Registration successful! Check your email for OTP.";
    }

    public String verifyOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getVerified()) {
                return "User is already verified!";
            }

            if (user.getOtpGeneratedTime().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES).isBefore(Instant.now())) {
                return "OTP expired! Please request a new one.";
            }

            if (user.getOtpAttempts() >= MAX_OTP_ATTEMPTS) {
                return "Maximum OTP attempts exceeded! Please request a new one.";
            }

            if (user.getOtp().equals(otp)) {
                user.setVerified(true);
                user.setOtp(null);
                user.setOtpAttempts(0);
                userRepository.save(user);
                return "Verification successful!";
            } else {
                user.setOtpAttempts(user.getOtpAttempts() + 1);
                userRepository.save(user);
                return "Invalid OTP!";
            }
        }
        return "User not found!";
    }

    public String resendOtp(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String otp = generateOtp();
            user.setOtp(otp);
            user.setOtpGeneratedTime(Instant.now());
            userRepository.save(user);
            emailService.sendOtpEmail(email, otp);
            return "OTP resent successfully!";
        }
        return "User not found!";
    }

    public String sendPasswordResetOtp(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return "User not found!";
        }

        User user = userOptional.get();
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant());
        user.setOtpAttempts(0);
        userRepository.save(user);

        // Send OTP Email
        emailService.sendOtpEmail(email, otp);
        return "OTP for password reset sent! Check your email.";
    }


    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
