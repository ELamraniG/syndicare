package com.syndicare.domain.dtos.tickets;

import com.syndicare.domain.entities.tickets.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketsGetAndPostResponseDto {
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
}
