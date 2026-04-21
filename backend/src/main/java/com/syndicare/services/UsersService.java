package com.syndicare.services;

import com.syndicare.mappers.UsersGetResponseMapper;
import com.syndicare.domain.entities.users.Role;
import com.syndicare.domain.entities.users.User;
import com.syndicare.domain.entities.UserPrincipal;
import com.syndicare.domain.dtos.users.UsersGetResponseDto;
import com.syndicare.repositories.users.UserRepository;
import com.syndicare.exceptions.ResourcesNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersGetResponseMapper usersGetResponseMapper;
    private final UserRepository userRepository;

    public List<UsersGetResponseDto> usersGetService() {
        return userRepository.findAll().stream()
                .map(usersGetResponseMapper::map)
                .toList();
    }

    public List<UsersGetResponseDto> usersGetByRoleService(Role role) {
        return userRepository.findByRole(role).stream()
                .map(usersGetResponseMapper::map)
                .toList();
    }

    public UsersGetResponseDto usersGetIdService(Long id) {
        return usersGetResponseMapper.map(userRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("User not found")));
    }

    public UsersGetResponseDto usersMeService() {
        return usersGetResponseMapper.map(getCurrentUser());
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal)
            return userPrincipal.getUser();
        if (principal instanceof User user)
            return user;

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourcesNotFoundException("User not found"));
    }
}
