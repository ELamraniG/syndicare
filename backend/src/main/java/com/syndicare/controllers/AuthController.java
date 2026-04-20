package com.syndicare.controllers;

import com.syndicare.domain.dtos.auth.*;
import com.syndicare.domain.dtos.users.UsersGetResponseDto;
import com.syndicare.services.AuthService;
import com.syndicare.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<AuthPostResponseDto> loginController(@Valid @RequestBody LoginDto dto) {
        AuthPostResponseDto response = authService.loginService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthPostResponseDto> registerController(@Valid @RequestBody RegisterDto dto) {
        AuthPostResponseDto response = authService.registerService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsersGetResponseDto> authMeController() {
        UsersGetResponseDto response = usersService.usersMeService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
