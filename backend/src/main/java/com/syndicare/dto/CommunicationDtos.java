package com.syndicare.dto;

import com.syndicare.domain.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

public class CommunicationDtos {

    // ===== Documents =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentResponse {
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

        public static DocumentResponse from(Document d) {
            return DocumentResponse.builder()
                    .id(d.getId())
                    .title(d.getTitle())
                    .description(d.getDescription())
                    .category(d.getCategory())
                    .originalFilename(d.getOriginalFilename())
                    .fileSize(d.getFileSize())
                    .contentType(d.getContentType())
                    .buildingId(d.getBuilding() != null ? d.getBuilding().getId() : null)
                    .buildingName(d.getBuilding() != null ? d.getBuilding().getName() : null)
                    .uploadedBy(d.getUploadedBy() != null
                            ? d.getUploadedBy().getFirstName() + " " + d.getUploadedBy().getLastName()
                            : null)
                    .uploadedAt(d.getUploadedAt())
                    .build();
        }
    }

    // ===== Announcements =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementRequest {
        @NotBlank private String title;
        @NotBlank private String content;
        private AnnouncementSeverity severity;
        private Long buildingId;
        private boolean pinned;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnnouncementResponse {
        private Long id;
        private String title;
        private String content;
        private AnnouncementSeverity severity;
        private Long buildingId;
        private String buildingName;
        private String authorName;
        private boolean pinned;
        private LocalDateTime createdAt;

        public static AnnouncementResponse from(Announcement a) {
            return AnnouncementResponse.builder()
                    .id(a.getId())
                    .title(a.getTitle())
                    .content(a.getContent())
                    .severity(a.getSeverity())
                    .buildingId(a.getBuilding() != null ? a.getBuilding().getId() : null)
                    .buildingName(a.getBuilding() != null ? a.getBuilding().getName() : null)
                    .authorName(a.getAuthor().getFirstName() + " " + a.getAuthor().getLastName())
                    .pinned(a.isPinned())
                    .createdAt(a.getCreatedAt())
                    .build();
        }
    }
}
