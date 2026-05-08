package com.syndicare.service;

import com.syndicare.domain.Announcement;
import com.syndicare.domain.AnnouncementSeverity;
import com.syndicare.dto.CommunicationDtos.*;
import com.syndicare.repository.AnnouncementRepository;
import com.syndicare.repository.BuildingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final BuildingRepository buildingRepository;
    private final UserService userService;

    public List<AnnouncementResponse> findAll() {
        return announcementRepository.findAllByOrderByPinnedDescCreatedAtDesc().stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList());
    }

    public AnnouncementResponse create(AnnouncementRequest req) {
        Announcement a = Announcement.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .severity(req.getSeverity() != null ? req.getSeverity() : AnnouncementSeverity.INFO)
                .author(userService.getCurrentUser())
                .pinned(req.isPinned())
                .build();
        if (req.getBuildingId() != null) {
            a.setBuilding(buildingRepository.findById(req.getBuildingId())
                    .orElseThrow(() -> new EntityNotFoundException("Building not found")));
        }
        return AnnouncementResponse.from(announcementRepository.save(a));
    }

    public AnnouncementResponse update(Long id, AnnouncementRequest req) {
        Announcement a = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));
        a.setTitle(req.getTitle());
        a.setContent(req.getContent());
        if (req.getSeverity() != null) a.setSeverity(req.getSeverity());
        a.setPinned(req.isPinned());
        return AnnouncementResponse.from(announcementRepository.save(a));
    }

    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }
}
