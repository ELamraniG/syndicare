package com.syndicare.controllers;

import com.syndicare.domain.entities.users.Role;
import com.syndicare.domain.dtos.users.UsersGetResponseDto;
import com.syndicare.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsersController {

    private final UsersService usersService;

    @GetMapping
    public ResponseEntity<List<UsersGetResponseDto>> usersGetController(@RequestParam(required = false) Role role) {
        List<UsersGetResponseDto> response = role == null ? usersService.usersGetService() : usersService.usersGetByRoleService(role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersGetResponseDto> usersGetIdController(@PathVariable Long id) {
        UsersGetResponseDto response = usersService.usersGetIdService(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
