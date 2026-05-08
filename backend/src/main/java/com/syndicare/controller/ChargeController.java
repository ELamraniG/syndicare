package com.syndicare.controller;

import com.syndicare.domain.Role;
import com.syndicare.dto.ChargeDtos.*;
import com.syndicare.service.ChargeService;
import com.syndicare.service.PdfService;
import com.syndicare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charges")
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;
    private final UserService userService;
    private final PdfService pdfService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChargeResponse>> findAll(
            @RequestParam(required = false) Long apartmentId,
            @RequestParam(required = false) Long buildingId) {
        if (apartmentId != null) return ResponseEntity.ok(chargeService.findByApartment(apartmentId));
        if (buildingId != null) return ResponseEntity.ok(chargeService.findByBuilding(buildingId));
        return ResponseEntity.ok(chargeService.findAll());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ChargeResponse>> mine() {
        var u = userService.getCurrentUser();
        if (u.getRole() == Role.ADMIN) return ResponseEntity.ok(chargeService.findAll());
        return ResponseEntity.ok(chargeService.findByOwner(u.getId()));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChargeResponse>> generate(@Valid @RequestBody GenerateChargesRequest req) {
        return ResponseEntity.ok(chargeService.generateForBuilding(req));
    }

    @PostMapping("/validate-payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChargeResponse> validatePayment(@Valid @RequestBody ValidatePaymentRequest req) {
        return ResponseEntity.ok(chargeService.validatePayment(req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chargeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<ByteArrayResource> downloadReceipt(@PathVariable Long id) {
        var charge = chargeService.get(id);
        byte[] pdf = pdfService.generateReceipt(charge);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "quittance-" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .body(new ByteArrayResource(pdf));
    }
}
