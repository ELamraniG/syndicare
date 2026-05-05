package com.syndicare.controllers;

import com.syndicare.domain.entities.users.User;
import com.syndicare.domain.dtos.dashboard.DashboardAdminGetResponseDto;
import com.syndicare.domain.dtos.dashboard.DashboardOwnerGetResponseDto;
import com.syndicare.services.DashboardService;
import com.syndicare.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UsersService usersService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAdminGetResponseDto> dashboardAdminGetController() {
        DashboardAdminGetResponseDto response = dashboardService.dashboardAdminGetService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/owner")
    public ResponseEntity<DashboardOwnerGetResponseDto> dashboardOwnerGetController() {
        User user = usersService.getCurrentUser();
        DashboardOwnerGetResponseDto response = dashboardService.dashboardOwnerGetService(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
