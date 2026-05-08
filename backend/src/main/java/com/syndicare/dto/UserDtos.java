package com.syndicare.dto;

import com.syndicare.domain.Role;
import com.syndicare.domain.User;
import lombok.*;

public class UserDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Role role;
        private boolean enabled;

        public static UserResponse from(User u) {
            return UserResponse.builder()
                    .id(u.getId())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .enabled(u.isEnabled())
                    .build();
        }
    }
}
