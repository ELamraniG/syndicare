package com.syndicare.services;

import com.syndicare.mappers.TicketsGetAndPostResponseMapper;
import com.syndicare.domain.entities.buildings.Apartment;
import com.syndicare.domain.entities.buildings.Building;
import com.syndicare.domain.entities.tickets.*;
import com.syndicare.domain.entities.users.User;
import com.syndicare.domain.dtos.tickets.*;
import com.syndicare.repositories.buildings.ApartmentRepository;
import com.syndicare.repositories.buildings.BuildingRepository;
import com.syndicare.repositories.tickets.TicketRepository;
import com.syndicare.exceptions.ResourcesNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketsService {
    private final TicketsGetAndPostResponseMapper ticketsGetAndPostResponseMapper;
    private final TicketRepository ticketRepository;
    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;
    private final UsersService usersService;
    private final FileStorageService fileStorageService;

    public List<TicketsGetAndPostResponseDto> ticketsGetService() {
        return ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ticketsGetAndPostResponseMapper::map)
                .toList();
    }

    public List<TicketsGetAndPostResponseDto> ticketsGetMeService() {
        Long userId = usersService.getCurrentUser().getId();
        return ticketRepository.findBySubmitterIdOrderByCreatedAtDesc(userId).stream()
                .map(ticketsGetAndPostResponseMapper::map)
                .toList();
    }

    public TicketsGetAndPostResponseDto ticketsGetIdService(Long id) {
        return ticketsGetAndPostResponseMapper.map(getTicketById(id));
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Ticket not found"));
    }

    public TicketsGetAndPostResponseDto ticketsPostService(TicketsPostRequestDto dto, MultipartFile photo) {
        User user = usersService.getCurrentUser();

        Ticket ticket = Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority() != null ? dto.getPriority() : TicketPriority.NORMAL)
                .status(TicketStatus.OPEN)
                .submitter(user)
                .build();

        if (dto.getApartmentId() != null) {
            ticket.setApartment(apartmentRepository.findById(dto.getApartmentId())
                    .orElseThrow(() -> new ResourcesNotFoundException("Apartment not found")));
            if (ticket.getApartment().getBuilding() != null) {
                ticket.setBuilding(ticket.getApartment().getBuilding());
            }
        }
        if (dto.getBuildingId() != null) {
            ticket.setBuilding(buildingRepository.findById(dto.getBuildingId())
                    .orElseThrow(() -> new ResourcesNotFoundException("Building not found")));
        }

        if (photo != null && !photo.isEmpty()) {
            ticket.setPhotoPath(fileStorageService.store(photo, "tickets"));
        }

        return ticketsGetAndPostResponseMapper.map(ticketRepository.save(ticket));
    }

    public TicketsGetAndPostResponseDto ticketsPatchService(Long id, TicketsPatchRequestDto dto) {
        Ticket ticket = getTicketById(id);
        if (dto.getStatus() != null) {
            ticket.setStatus(dto.getStatus());
            if (dto.getStatus() == TicketStatus.RESOLVED || dto.getStatus() == TicketStatus.CLOSED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
        if (dto.getPriority() != null) ticket.setPriority(dto.getPriority());
        if (dto.getAdminNotes() != null) ticket.setAdminNotes(dto.getAdminNotes());
        return ticketsGetAndPostResponseMapper.map(ticketRepository.save(ticket));
    }

    public void ticketsDeleteService(Long id) {
        Ticket ticket = getTicketById(id);
        if (ticket.getPhotoPath() != null) fileStorageService.delete(ticket.getPhotoPath());
        ticketRepository.deleteById(id);
    }
}
