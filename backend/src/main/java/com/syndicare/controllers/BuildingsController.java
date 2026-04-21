package com.syndicare.controllers;

import com.syndicare.domain.dtos.buildings.BuildingsGetAndPostResponseDto;
import com.syndicare.domain.dtos.buildings.BuildingsPostAndPutRequestDto;
import com.syndicare.services.BuildingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingsController {

    private final BuildingsService buildingsService;

    @GetMapping
    public ResponseEntity<List<BuildingsGetAndPostResponseDto>> buildingsGetController() {
        List<BuildingsGetAndPostResponseDto> response = buildingsService.buildingsGetService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingsGetAndPostResponseDto> buildingsGetIdController(@PathVariable Long id) {
        BuildingsGetAndPostResponseDto response = buildingsService.buildingsGetIdService(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingsGetAndPostResponseDto> buildingsPostController(@Valid @RequestBody BuildingsPostAndPutRequestDto dto) {
        BuildingsGetAndPostResponseDto response = buildingsService.buildingsPostService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingsGetAndPostResponseDto> buildingsPutController(@PathVariable Long id, @Valid @RequestBody BuildingsPostAndPutRequestDto dto) {
        BuildingsGetAndPostResponseDto response = buildingsService.buildingsPutService(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> buildingsDeleteController(@PathVariable Long id) {
        buildingsService.buildingsDeleteService(id);
        return ResponseEntity.noContent().build();
    }
}
