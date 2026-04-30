package com.syndicare.controllers;

import com.syndicare.domain.dtos.announcements.AnnouncementsGetAndPostResponseDto;
import com.syndicare.domain.dtos.announcements.AnnouncementsPostAndPutRequestDto;
import com.syndicare.services.AnnouncementsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementsController {

    private final AnnouncementsService announcementsService;

    @GetMapping
    public ResponseEntity<List<AnnouncementsGetAndPostResponseDto>> announcementsGetController() {
        List<AnnouncementsGetAndPostResponseDto> response = announcementsService.announcementsGetService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementsGetAndPostResponseDto> announcementsPostController(@Valid @RequestBody AnnouncementsPostAndPutRequestDto dto) {
        AnnouncementsGetAndPostResponseDto response = announcementsService.announcementsPostService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnnouncementsGetAndPostResponseDto> announcementsPutController(@PathVariable Long id,
            @Valid @RequestBody AnnouncementsPostAndPutRequestDto dto) {
        AnnouncementsGetAndPostResponseDto response = announcementsService.announcementsPutService(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> announcementsDeleteController(@PathVariable Long id) {
        announcementsService.announcementsDeleteService(id);
        return ResponseEntity.noContent().build();
    }
}
