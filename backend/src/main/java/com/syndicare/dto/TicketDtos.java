package com.syndicare.dto;

import com.syndicare.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class TicketDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketRequest {
        @NotBlank
        private String title;
        @NotBlank
        private String description;
        @NotNull
        private TicketCategory category;
        private TicketPriority priority;
        private Long apartmentId;
        private Long buildingId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTicketRequest {
        private TicketStatus status;
        private TicketPriority priority;
        private String adminNotes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TicketResponse {
        private Long id;
        private String title;
        private String description;
        private TicketCategory category;
        private TicketStatus status;
        private TicketPriority priority;
        private Long submitterId;
        private String submitterName;
        private Long apartmentId;
        private String apartmentNumber;
        private Long buildingId;
        private String buildingName;
        private String photoPath;
        private String adminNotes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime resolvedAt;

        public static TicketResponse from(Ticket t) {
            return TicketResponse.builder()
                    .id(t.getId())
                    .title(t.getTitle())
                    .description(t.getDescription())
                    .category(t.getCategory())
                    .status(t.getStatus())
                    .priority(t.getPriority())
                    .submitterId(t.getSubmitter().getId())
                    .submitterName(t.getSubmitter().getFirstName() + " " + t.getSubmitter().getLastName())
                    .apartmentId(t.getApartment() != null ? t.getApartment().getId() : null)
                    .apartmentNumber(t.getApartment() != null ? t.getApartment().getNumber() : null)
                    .buildingId(t.getBuilding() != null ? t.getBuilding().getId() : null)
                    .buildingName(t.getBuilding() != null ? t.getBuilding().getName() : null)
                    .photoPath(t.getPhotoPath())
                    .adminNotes(t.getAdminNotes())
                    .createdAt(t.getCreatedAt())
                    .updatedAt(t.getUpdatedAt())
                    .resolvedAt(t.getResolvedAt())
                    .build();
        }
    }
}
