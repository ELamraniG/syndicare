package com.syndicare.services;

import com.syndicare.domain.dtos.documents.DocumentsGetAndPostResponseDto;
import com.syndicare.domain.entities.documents.Document;
import com.syndicare.domain.entities.documents.DocumentCategory;
import com.syndicare.exceptions.InvalidRequestException;
import com.syndicare.exceptions.ResourcesNotFoundException;
import com.syndicare.repositories.buildings.BuildingRepository;
import com.syndicare.repositories.documents.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentsService {
    private final DocumentRepository documentRepository;
    private final BuildingRepository buildingRepository;
    private final FileStorageService fileStorageService;
    private final UsersService usersService;

    public List<DocumentsGetAndPostResponseDto> documentsGetService() {
        return mapDocuments(documentRepository.findAllByOrderByUploadedAtDesc());
    }

    public List<DocumentsGetAndPostResponseDto> documentsGetByCategoryService(DocumentCategory category) {
        return mapDocuments(documentRepository.findByCategoryOrderByUploadedAtDesc(category));
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Document not found"));
    }

    public DocumentsGetAndPostResponseDto documentsPostService(MultipartFile file, String title, String description,
                                                                 DocumentCategory category, Long buildingId) {
        if (file == null || file.isEmpty())
            throw new InvalidRequestException("File required");

        String path = fileStorageService.store(file, "documents");
        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setCategory(category == null ? DocumentCategory.OTHER : category);
        document.setFilePath(path);
        document.setOriginalFilename(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setContentType(file.getContentType());
        document.setUploadedBy(usersService.getCurrentUser());

        if (buildingId != null) {
            document.setBuilding(buildingRepository.findById(buildingId)
                    .orElseThrow(() -> new ResourcesNotFoundException("Building not found")));
        }
        documentRepository.save(document);
        return toDto(document);
    }

    public void documentsDeleteService(Long id) {
        Document document = getDocumentById(id);
        fileStorageService.delete(document.getFilePath());
        documentRepository.delete(document);
    }

    private List<DocumentsGetAndPostResponseDto> mapDocuments(List<Document> documents) {
        List<DocumentsGetAndPostResponseDto> result = new ArrayList<>();
        for (Document document : documents)
            result.add(toDto(document));
        return result;
    }

    private DocumentsGetAndPostResponseDto toDto(Document document) {
        DocumentsGetAndPostResponseDto dto = new DocumentsGetAndPostResponseDto();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setCategory(document.getCategory());
        dto.setOriginalFilename(document.getOriginalFilename());
        dto.setFileSize(document.getFileSize());
        dto.setContentType(document.getContentType());
        dto.setUploadedAt(document.getUploadedAt());
        if (document.getBuilding() != null) {
            dto.setBuildingId(document.getBuilding().getId());
            dto.setBuildingName(document.getBuilding().getName());
        }
        if (document.getUploadedBy() != null)
            dto.setUploadedBy(document.getUploadedBy().getFirstName() + " " + document.getUploadedBy().getLastName());
        return dto;
    }
}
