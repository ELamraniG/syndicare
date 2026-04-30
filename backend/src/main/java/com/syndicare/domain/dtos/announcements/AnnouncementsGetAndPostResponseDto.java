package com.syndicare.domain.dtos.announcements;

import com.syndicare.domain.entities.announcements.AnnouncementSeverity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementsGetAndPostResponseDto {
    private Long id;
    private String title;
    private String content;
    private AnnouncementSeverity severity;
    private Long buildingId;
    private String buildingName;
    private String authorName;
    private boolean pinned;
    private LocalDateTime createdAt;
}
