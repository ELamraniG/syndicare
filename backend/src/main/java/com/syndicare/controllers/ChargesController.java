package com.syndicare.controllers;

import com.syndicare.domain.entities.charges.Charge;
import com.syndicare.domain.entities.users.User;
import com.syndicare.domain.entities.users.Role;
import com.syndicare.domain.dtos.charges.*;
import com.syndicare.services.ChargesService;
import com.syndicare.services.PdfService;
import com.syndicare.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charges")
@RequiredArgsConstructor
public class ChargesController {

    private final ChargesService chargesService;
    private final UsersService usersService;
    private final PdfService pdfService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChargesGetAndPostResponseDto>> chargesGetController(
            @RequestParam(required = false) Long apartmentId,
            @RequestParam(required = false) Long buildingId) {
        if (apartmentId != null)
            return ResponseEntity.status(HttpStatus.OK).body(chargesService.chargesGetByApartmentService(apartmentId));
        if (buildingId != null)
            return ResponseEntity.status(HttpStatus.OK).body(chargesService.chargesGetByBuildingService(buildingId));
        List<ChargesGetAndPostResponseDto> response = chargesService.chargesGetService();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ChargesGetAndPostResponseDto>> chargesGetMeController() {
        User user = usersService.getCurrentUser();
        if (user.getRole() == Role.ADMIN)
            return ResponseEntity.status(HttpStatus.OK).body(chargesService.chargesGetService());
        List<ChargesGetAndPostResponseDto> response = chargesService.chargesGetByOwnerService(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChargesGetAndPostResponseDto>> chargesGeneratePostController(@Valid @RequestBody ChargesPostRequestDto dto) {
        List<ChargesGetAndPostResponseDto> response = chargesService.chargesGeneratePostService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/validate-payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChargesGetAndPostResponseDto> chargesPaymentPostController(@Valid @RequestBody ChargesPaymentPostRequestDto dto) {
        ChargesGetAndPostResponseDto response = chargesService.chargesPaymentPostService(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> chargesDeleteController(@PathVariable Long id) {
        chargesService.chargesDeleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<ByteArrayResource> chargesReceiptGetController(@PathVariable Long id) {
        Charge charge = chargesService.getChargeById(id);
        byte[] pdf = pdfService.receiptGetService(charge);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "quittance-" + id + ".pdf");

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .contentLength(pdf.length)
                .body(new ByteArrayResource(pdf));
    }
}
