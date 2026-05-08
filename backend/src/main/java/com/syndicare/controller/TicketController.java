package com.syndicare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicare.domain.Role;
import com.syndicare.dto.TicketDtos.*;
import com.syndicare.service.TicketService;
import com.syndicare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<TicketResponse>> findAll() {
        var u = userService.getCurrentUser();
        if (u.getRole() == Role.ADMIN) return ResponseEntity.ok(ticketService.findAll());
        return ResponseEntity.ok(ticketService.findMine());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    /** Multipart endpoint: 'data' is JSON for TicketRequest, optional 'photo' file. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketResponse> create(
            @RequestPart("data") String data,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        TicketRequest req = objectMapper.readValue(data, TicketRequest.class);
        return ResponseEntity.ok(ticketService.create(req, photo));
    }

    /** JSON-only fallback for clients without file upload. */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketResponse> createJson(@Valid @RequestBody TicketRequest req) {
        return ResponseEntity.ok(ticketService.create(req, null));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id,
                                                 @RequestBody UpdateTicketRequest req) {
        return ResponseEntity.ok(ticketService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
