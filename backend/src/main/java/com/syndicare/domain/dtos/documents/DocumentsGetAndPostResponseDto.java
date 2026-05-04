package com.syndicare.domain.dtos.documents;

import com.syndicare.domain.entities.documents.DocumentCategory;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentsGetAndPostResponseDto {
    private Long id;
    private String title;
    private String description;
    private DocumentCategory category;
    private String originalFilename;
    private Long fileSize;
    private String contentType;
    private Long buildingId;
    private String buildingName;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
