package com.syndicare.controller;

import com.syndicare.dto.ApartmentDtos.*;
import com.syndicare.service.ApartmentService;
import com.syndicare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ApartmentResponse>> findAll(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long ownerId) {
        if (buildingId != null) return ResponseEntity.ok(apartmentService.findByBuilding(buildingId));
        if (ownerId != null) return ResponseEntity.ok(apartmentService.findByOwner(ownerId));
        return ResponseEntity.ok(apartmentService.findAll());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ApartmentResponse>> mine() {
        Long userId = userService.getCurrentUser().getId();
        // Owners see what they own, residents see where they live
        List<ApartmentResponse> owned = apartmentService.findByOwner(userId);
        if (!owned.isEmpty()) return ResponseEntity.ok(owned);
        return ResponseEntity.ok(apartmentService.findByResident(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(apartmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApartmentResponse> create(@Valid @RequestBody ApartmentRequest req) {
        return ResponseEntity.ok(apartmentService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApartmentResponse> update(@PathVariable Long id, @Valid @RequestBody ApartmentRequest req) {
        return ResponseEntity.ok(apartmentService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        apartmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
