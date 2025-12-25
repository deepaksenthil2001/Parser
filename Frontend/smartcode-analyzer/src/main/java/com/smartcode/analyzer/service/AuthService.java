package com.smartcode.analyzer.service;

import com.smartcode.analyzer.model.User;
import com.smartcode.analyzer.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(String name, String email, String plainPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        String hash = passwordEncoder.encode(plainPassword);
        User u = new User(name, email, hash);
        return userRepository.save(u);
    }

    public User login(String email, String plainPassword) {
        Optional<User> ou = userRepository.findByEmail(email);
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");

        User u = ou.get();
        if (!passwordEncoder.matches(plainPassword, u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return u;
    }
}
