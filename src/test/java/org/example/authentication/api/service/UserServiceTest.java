package org.example.authentication.api.service;

import org.example.authentication.api.model.User;
import org.example.authentication.api.model.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSendPasswordResetOtp_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        String result = userService.sendPasswordResetOtp(email);

        // Assert
        assertEquals("User not found!", result);
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @Test
    public void testSendPasswordResetOtp_UserFound() {
        // Arrange
        String email = "existing@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        String result = userService.sendPasswordResetOtp(email);

        // Assert
        assertEquals("OTP for password reset sent! Check your email.", result);
        verify(emailService, times(1)).sendOtpEmail(eq(email), anyString());
    }
}

