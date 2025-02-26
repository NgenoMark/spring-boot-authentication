package org.example.authentication.api.service;

import org.example.authentication.api.model.User;
import org.example.authentication.api.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        newUser.setOtpGeneratedTime(LocalDateTime.now());
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

            // Check if the user is already verified
            if (user.getIsVerified()) {
                return "User is already verified!";
            }

            // Check OTP expiration
            if (user.getOtpGeneratedTime().plusMinutes(OTP_EXPIRATION_MINUTES).isBefore(LocalDateTime.now())) {
                return "OTP expired! Please request a new one.";
            }

            // Check OTP attempts
            if (user.getOtpAttempts() >= MAX_OTP_ATTEMPTS) {
                return "Maximum OTP attempts exceeded! Please request a new one.";
            }

            // Validate OTP
            if (user.getOtp().equals(otp)) {
                user.setIsVerified(true);
                user.setOtp(null); // Clear OTP after successful verification
                user.setOtpAttempts(0); // Reset OTP attempts
                userRepository.save(user);
                return "Verification successful!";
            } else {
                user.setOtpAttempts(user.getOtpAttempts() + 1); // Increment OTP attempts
                userRepository.save(user);
                return "Invalid OTP!";
            }
        }
        return "User not found!";
    }

    private String generateOtp() {
        // Ensure OTP is 6 digits (e.g., 000123 instead of 123)
        return String.format("%06d", new Random().nextInt(999999));
    }
}
