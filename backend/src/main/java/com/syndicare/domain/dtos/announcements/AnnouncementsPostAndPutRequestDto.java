package com.syndicare.domain.dtos.announcements;

import com.syndicare.domain.entities.announcements.AnnouncementSeverity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementsPostAndPutRequestDto {
    @NotBlank private String title;
    @NotBlank private String content;
    private AnnouncementSeverity severity;
    private Long buildingId;
    private boolean pinned;
}
