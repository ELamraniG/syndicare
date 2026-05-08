package com.syndicare.service;

import com.syndicare.domain.Document;
import com.syndicare.domain.DocumentCategory;
import com.syndicare.dto.CommunicationDtos.DocumentResponse;
import com.syndicare.repository.BuildingRepository;
import com.syndicare.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final BuildingRepository buildingRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    public List<DocumentResponse> findAll() {
        return documentRepository.findAllByOrderByUploadedAtDesc().stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<DocumentResponse> findByCategory(DocumentCategory category) {
        return documentRepository.findByCategoryOrderByUploadedAtDesc(category).stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
    }

    public Document get(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
    }

    public DocumentResponse upload(MultipartFile file, String title, String description,
                                   DocumentCategory category, Long buildingId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File required");
        }
        String path = fileStorageService.store(file, "documents");

        Document d = Document.builder()
                .title(title)
                .description(description)
                .category(category != null ? category : DocumentCategory.OTHER)
                .filePath(path)
                .originalFilename(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .uploadedBy(userService.getCurrentUser())
                .build();
        if (buildingId != null) {
            d.setBuilding(buildingRepository.findById(buildingId)
                    .orElseThrow(() -> new EntityNotFoundException("Building not found")));
        }
        return DocumentResponse.from(documentRepository.save(d));
    }

    public void delete(Long id) {
        Document d = get(id);
        fileStorageService.delete(d.getFilePath());
        documentRepository.deleteById(id);
    }
}
