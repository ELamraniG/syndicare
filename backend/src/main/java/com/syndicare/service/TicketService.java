package com.syndicare.service;

import com.syndicare.domain.*;
import com.syndicare.dto.TicketDtos.*;
import com.syndicare.repository.ApartmentRepository;
import com.syndicare.repository.BuildingRepository;
import com.syndicare.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public List<TicketResponse> findAll() {
        return ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TicketResponse::from)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> findMine() {
        Long userId = userService.getCurrentUser().getId();
        return ticketRepository.findBySubmitterIdOrderByCreatedAtDesc(userId).stream()
                .map(TicketResponse::from)
                .collect(Collectors.toList());
    }

    public TicketResponse findById(Long id) {
        return TicketResponse.from(get(id));
    }

    public Ticket get(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }

    public TicketResponse create(TicketRequest req, MultipartFile photo) {
        User user = userService.getCurrentUser();

        Ticket t = Ticket.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .priority(req.getPriority() != null ? req.getPriority() : TicketPriority.NORMAL)
                .status(TicketStatus.OPEN)
                .submitter(user)
                .build();

        if (req.getApartmentId() != null) {
            t.setApartment(apartmentRepository.findById(req.getApartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Apartment not found")));
            if (t.getApartment().getBuilding() != null) {
                t.setBuilding(t.getApartment().getBuilding());
            }
        }
        if (req.getBuildingId() != null) {
            t.setBuilding(buildingRepository.findById(req.getBuildingId())
                    .orElseThrow(() -> new EntityNotFoundException("Building not found")));
        }

        if (photo != null && !photo.isEmpty()) {
            t.setPhotoPath(fileStorageService.store(photo, "tickets"));
        }

        return TicketResponse.from(ticketRepository.save(t));
    }

    public TicketResponse update(Long id, UpdateTicketRequest req) {
        Ticket t = get(id);
        if (req.getStatus() != null) {
            t.setStatus(req.getStatus());
            if (req.getStatus() == TicketStatus.RESOLVED || req.getStatus() == TicketStatus.CLOSED) {
                t.setResolvedAt(LocalDateTime.now());
            }
        }
        if (req.getPriority() != null) t.setPriority(req.getPriority());
        if (req.getAdminNotes() != null) t.setAdminNotes(req.getAdminNotes());
        return TicketResponse.from(ticketRepository.save(t));
    }

    public void delete(Long id) {
        Ticket t = get(id);
        if (t.getPhotoPath() != null) fileStorageService.delete(t.getPhotoPath());
        ticketRepository.deleteById(id);
    }
}
