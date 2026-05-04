package com.syndicare.controllers;

import com.syndicare.domain.entities.documents.Document;
import com.syndicare.domain.entities.documents.DocumentCategory;
import com.syndicare.domain.dtos.documents.DocumentsGetAndPostResponseDto;
import com.syndicare.services.DocumentsService;
import com.syndicare.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentsController {

    private final DocumentsService documentsService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<DocumentsGetAndPostResponseDto>> documentsGetController(
            @RequestParam(required = false) DocumentCategory category) {
        return ResponseEntity.status(HttpStatus.OK).body(category == null
                ? documentsService.documentsGetService()
                : documentsService.documentsGetByCategoryService(category));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentsGetAndPostResponseDto> documentsPostController(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) DocumentCategory category,
            @RequestParam(value = "buildingId", required = false) Long buildingId) {
        DocumentsGetAndPostResponseDto response = documentsService.documentsPostService(file, title, description, category, buildingId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> documentsDownloadGetController(@PathVariable Long id) {
        Document document = documentsService.getDocumentById(id);
        Resource resource = fileStorageService.load(document.getFilePath());

        String filename = document.getOriginalFilename() != null ? document.getOriginalFilename() : "document";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename);
        if (document.getContentType() != null) {
            headers.setContentType(MediaType.parseMediaType(document.getContentType()));
        }
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> documentsDeleteController(@PathVariable Long id) {
        documentsService.documentsDeleteService(id);
        return ResponseEntity.noContent().build();
    }
}
