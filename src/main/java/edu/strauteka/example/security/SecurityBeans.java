package edu.strauteka.example.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityBeans {
    @Bean
    public Algorithm createPasswordEncoder() {
        return Algorithm.HMAC256("superlongsecret".getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
