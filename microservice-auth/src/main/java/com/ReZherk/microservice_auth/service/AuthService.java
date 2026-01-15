package com.ReZherk.microservice_auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ReZherk.common.dto.request.UserRequestDto;
import com.ReZherk.microservice_auth.client.UserClient;
import com.ReZherk.microservice_auth.config.JwtUtil;
import com.ReZherk.microservice_auth.dto.AuthResponseDto;
import com.ReZherk.microservice_auth.dto.LoginRequestDto;
import com.ReZherk.microservice_auth.dto.RegisterRequestDto;
import com.ReZherk.microservice_auth.entity.User;
import com.ReZherk.microservice_auth.exception.ExternalServiceException;
import com.ReZherk.microservice_auth.mapper.AuthMapper;
import com.ReZherk.microservice_auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    private final AuthMapper authMapper;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .build();

        user = userRepository.save(user);

        UserRequestDto dto = authMapper.mapToUserRequestDto(request);
        try {
            userClient.registerUser(dto);
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to register user in Users service", e);
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponseDto(token, user.getUsername(), user.getRole());
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponseDto(token, user.getUsername(), user.getRole());
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, username);
        } catch (Exception e) {
            return false;
        }
    }
}