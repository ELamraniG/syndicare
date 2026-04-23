package com.syndicare.controllers;

import com.syndicare.domain.dtos.apartments.ApartmentsGetAndPostResponseDto;
import com.syndicare.domain.dtos.apartments.ApartmentsPostAndPutRequestDto;
import com.syndicare.services.ApartmentsService;
import com.syndicare.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor
public class ApartmentsController {

    private final ApartmentsService apartmentsService;
    private final UsersService usersService;

    @GetMapping
    public ResponseEntity<List<ApartmentsGetAndPostResponseDto>> apartmentsGetController(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long ownerId) {
        if (buildingId != null)
            return ResponseEntity.status(HttpStatus.OK).body(apartmentsService.apartmentsGetByBuildingService(buildingId));
        if (ownerId != null)
            return ResponseEntity.status(HttpStatus.OK).body(apartmentsService.apartmentsGetByOwnerService(ownerId));
        List<ApartmentsGetAndPostResponseDto> response = apartmentsService.apartmentsGetService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ApartmentsGetAndPostResponseDto>> apartmentsGetMeController() {
        Long userId = usersService.getCurrentUser().getId();

        List<ApartmentsGetAndPostResponseDto> owned = apartmentsService.apartmentsGetByOwnerService(userId);
        if (!owned.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(owned);
        List<ApartmentsGetAndPostResponseDto> response = apartmentsService.apartmentsGetByResidentService(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentsGetAndPostResponseDto> apartmentsGetIdController(@PathVariable Long id) {
        ApartmentsGetAndPostResponseDto response = apartmentsService.apartmentsGetIdService(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApartmentsGetAndPostResponseDto> apartmentsPostController(@Valid @RequestBody ApartmentsPostAndPutRequestDto dto) {
        ApartmentsGetAndPostResponseDto response = apartmentsService.apartmentsPostService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApartmentsGetAndPostResponseDto> apartmentsPutController(@PathVariable Long id, @Valid @RequestBody ApartmentsPostAndPutRequestDto dto) {
        ApartmentsGetAndPostResponseDto response = apartmentsService.apartmentsPutService(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> apartmentsDeleteController(@PathVariable Long id) {
        apartmentsService.apartmentsDeleteService(id);
        return ResponseEntity.noContent().build();
    }
}
