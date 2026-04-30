package com.syndicare.services;

import com.syndicare.domain.dtos.announcements.AnnouncementsGetAndPostResponseDto;
import com.syndicare.domain.dtos.announcements.AnnouncementsPostAndPutRequestDto;
import com.syndicare.domain.entities.announcements.Announcement;
import com.syndicare.domain.entities.announcements.AnnouncementSeverity;
import com.syndicare.exceptions.ResourcesNotFoundException;
import com.syndicare.repositories.announcements.AnnouncementRepository;
import com.syndicare.repositories.buildings.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementsService {
    private final AnnouncementRepository announcementRepository;
    private final BuildingRepository buildingRepository;
    private final UsersService usersService;

    public List<AnnouncementsGetAndPostResponseDto> announcementsGetService() {
        List<AnnouncementsGetAndPostResponseDto> response = new ArrayList<>();
        for (Announcement a : announcementRepository.findAllByOrderByPinnedDescCreatedAtDesc())
            response.add(toResponse(a));
        return response;
    }

    public AnnouncementsGetAndPostResponseDto announcementsPostService(AnnouncementsPostAndPutRequestDto dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setSeverity(dto.getSeverity() == null ? AnnouncementSeverity.INFO : dto.getSeverity());
        announcement.setAuthor(usersService.getCurrentUser());
        announcement.setPinned(dto.isPinned());

        if (dto.getBuildingId() != null) {
            announcement.setBuilding(buildingRepository.findById(dto.getBuildingId())
                    .orElseThrow(() -> new ResourcesNotFoundException("Building not found")));
        }
        return toResponse(announcementRepository.save(announcement));
    }

    public AnnouncementsGetAndPostResponseDto announcementsPutService(Long id, AnnouncementsPostAndPutRequestDto dto) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Announcement not found"));
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        if (dto.getSeverity() != null)
            announcement.setSeverity(dto.getSeverity());
        announcement.setPinned(dto.isPinned());
        announcementRepository.save(announcement);
        return toResponse(announcement);
    }

    public void announcementsDeleteService(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementsGetAndPostResponseDto toResponse(Announcement a) {
        AnnouncementsGetAndPostResponseDto dto = new AnnouncementsGetAndPostResponseDto();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setSeverity(a.getSeverity());
        dto.setPinned(a.isPinned());
        dto.setCreatedAt(a.getCreatedAt());
        if (a.getBuilding() != null) {
            dto.setBuildingId(a.getBuilding().getId());
            dto.setBuildingName(a.getBuilding().getName());
        }
        if (a.getAuthor() != null)
            dto.setAuthorName(a.getAuthor().getFirstName() + " " + a.getAuthor().getLastName());
        return dto;
    }
}
