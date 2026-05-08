package com.syndicare.controller;

import com.syndicare.dto.DashboardDtos.*;
import com.syndicare.service.DashboardService;
import com.syndicare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse> admin() {
        return ResponseEntity.ok(dashboardService.adminStats());
    }

    @GetMapping("/owner")
    public ResponseEntity<OwnerDashboardResponse> owner() {
        var u = userService.getCurrentUser();
        return ResponseEntity.ok(dashboardService.ownerStats(u.getId()));
    }
}
