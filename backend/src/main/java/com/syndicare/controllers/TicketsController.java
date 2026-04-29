package com.syndicare.controllers;

import com.syndicare.domain.entities.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicare.domain.entities.users.Role;
import com.syndicare.domain.dtos.tickets.*;
import com.syndicare.services.TicketsService;
import com.syndicare.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketsController {

    private final TicketsService ticketsService;
    private final UsersService usersService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<TicketsGetAndPostResponseDto>> ticketsGetController() {
        User user = usersService.getCurrentUser();
        if (user.getRole() == Role.ADMIN)
            return ResponseEntity.status(HttpStatus.OK).body(ticketsService.ticketsGetService());
        List<TicketsGetAndPostResponseDto> response = ticketsService.ticketsGetMeService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketsGetAndPostResponseDto> ticketsGetIdController(@PathVariable Long id) {
        TicketsGetAndPostResponseDto response = ticketsService.ticketsGetIdService(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketsGetAndPostResponseDto> ticketsPostController(
            @RequestPart("data") String data,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        TicketsPostRequestDto dto = objectMapper.readValue(data, TicketsPostRequestDto.class);
        TicketsGetAndPostResponseDto response = ticketsService.ticketsPostService(dto, photo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketsGetAndPostResponseDto> ticketsPostJsonController(@Valid @RequestBody TicketsPostRequestDto dto) {
        TicketsGetAndPostResponseDto response = ticketsService.ticketsPostService(dto, null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketsGetAndPostResponseDto> ticketsPatchController(@PathVariable Long id,
            @RequestBody TicketsPatchRequestDto dto) {
        TicketsGetAndPostResponseDto response = ticketsService.ticketsPatchService(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> ticketsDeleteController(@PathVariable Long id) {
        ticketsService.ticketsDeleteService(id);
        return ResponseEntity.noContent().build();
    }
}
