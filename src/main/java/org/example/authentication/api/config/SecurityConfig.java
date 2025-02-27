package org.example.authentication.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF if not needed (New approach in Spring Security 6.x)
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // Use requestMatchers() instead of antMatchers()
                        .requestMatchers("/register", "/verify-otp", "/resend-otp", "/send-password-reset-otp").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    // Old Code (Spring Security 5.x and below)
    // The following code used WebSecurityConfigurerAdapter which is removed in Spring Security 6.x

    // public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //     @Override
    //     protected void configure(HttpSecurity http) throws Exception {
    //         http.csrf().disable()  // Disable CSRF if not needed
    //                 .authorizeRequests()
    //                 .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll(); // Allow access to login endpoint
    //         //.anyRequest().authenticated();  // All other requests must be authenticated
    //     }

    // Explanation of Changes:
    // 1. WebSecurityConfigurerAdapter is removed in Spring Security 6.x.
    //    - Solution: Use SecurityFilterChain bean instead.
    // 2. csrf() is now configured using a lambda style.
    // 3. authorizeRequests() and antMatchers() are deprecated.
    //    - Solution: Use authorizeHttpRequests() and requestMatchers() instead.
    // 4. The new approach is more modular and functional, promoting better readability and maintainability.
    // 5. http.build() is now required at the end to build the SecurityFilterChain.

    // }
    */
}
