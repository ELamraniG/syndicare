package com.syndicare.controller;

import com.syndicare.domain.Document;
import com.syndicare.domain.DocumentCategory;
import com.syndicare.dto.CommunicationDtos.DocumentResponse;
import com.syndicare.service.DocumentService;
import com.syndicare.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> findAll(
            @RequestParam(required = false) DocumentCategory category) {
        return ResponseEntity.ok(category == null
                ? documentService.findAll()
                : documentService.findByCategory(category));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) DocumentCategory category,
            @RequestParam(value = "buildingId", required = false) Long buildingId) {
        return ResponseEntity.ok(documentService.upload(file, title, description, category, buildingId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Document d = documentService.get(id);
        Resource resource = fileStorageService.load(d.getFilePath());

        String filename = d.getOriginalFilename() != null ? d.getOriginalFilename() : "document";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename);
        if (d.getContentType() != null) {
            headers.setContentType(MediaType.parseMediaType(d.getContentType()));
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
