package com.syndicare.service;

import com.syndicare.domain.Apartment;
import com.syndicare.domain.Building;
import com.syndicare.domain.User;
import com.syndicare.dto.ApartmentDtos.*;
import com.syndicare.repository.ApartmentRepository;
import com.syndicare.repository.BuildingRepository;
import com.syndicare.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;

    public List<ApartmentResponse> findAll() {
        return apartmentRepository.findAll().stream()
                .map(ApartmentResponse::from)
                .collect(Collectors.toList());
    }

    public List<ApartmentResponse> findByBuilding(Long buildingId) {
        return apartmentRepository.findByBuildingId(buildingId).stream()
                .map(ApartmentResponse::from)
                .collect(Collectors.toList());
    }

    public List<ApartmentResponse> findByOwner(Long ownerId) {
        return apartmentRepository.findByOwnerId(ownerId).stream()
                .map(ApartmentResponse::from)
                .collect(Collectors.toList());
    }

    public List<ApartmentResponse> findByResident(Long residentId) {
        return apartmentRepository.findByResidentId(residentId).stream()
                .map(ApartmentResponse::from)
                .collect(Collectors.toList());
    }

    public ApartmentResponse findById(Long id) {
        return ApartmentResponse.from(get(id));
    }

    public Apartment get(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Apartment not found"));
    }

    public ApartmentResponse create(ApartmentRequest req) {
        Apartment a = build(new Apartment(), req);
        return ApartmentResponse.from(apartmentRepository.save(a));
    }

    public ApartmentResponse update(Long id, ApartmentRequest req) {
        Apartment a = get(id);
        build(a, req);
        return ApartmentResponse.from(apartmentRepository.save(a));
    }

    public void delete(Long id) {
        apartmentRepository.deleteById(id);
    }

    private Apartment build(Apartment a, ApartmentRequest req) {
        a.setNumber(req.getNumber());
        a.setFloor(req.getFloor());
        a.setSurface(req.getSurface());

        Building b = buildingRepository.findById(req.getBuildingId())
                .orElseThrow(() -> new EntityNotFoundException("Building not found"));
        a.setBuilding(b);

        if (req.getOwnerId() != null) {
            User owner = userRepository.findById(req.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
            a.setOwner(owner);
        } else {
            a.setOwner(null);
        }

        if (req.getResidentId() != null) {
            User resident = userRepository.findById(req.getResidentId())
                    .orElseThrow(() -> new EntityNotFoundException("Resident not found"));
            a.setResident(resident);
        } else {
            a.setResident(null);
        }
        return a;
    }
}
