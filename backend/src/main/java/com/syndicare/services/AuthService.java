package com.syndicare.services;

import com.syndicare.exceptions.InvalidRequestException;
import com.syndicare.domain.entities.users.Role;
import com.syndicare.domain.entities.users.User;
import com.syndicare.domain.entities.UserPrincipal;
import com.syndicare.domain.dtos.auth.*;
import com.syndicare.repositories.users.UserRepository;
import com.syndicare.security.JjwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JjwtService jjwtService;
    private final AuthenticationManager authenticationManager;

    public AuthPostResponseDto registerService(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new InvalidRequestException("Email already in use");
        }
        Role role = dto.getRole() != null ? dto.getRole() : Role.OWNER;

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .role(role)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        String token = jjwtService.generateToken(new UserPrincipal(user));
        return buildAuthResponse(token, user);
    }

    public AuthPostResponseDto loginService(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = jjwtService.generateToken(new UserPrincipal(user));
        return buildAuthResponse(token, user);
    }

    private AuthPostResponseDto buildAuthResponse(String token, User user) {
        return AuthPostResponseDto.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
