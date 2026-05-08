package com.syndicare.service;

import com.syndicare.domain.Building;
import com.syndicare.dto.BuildingDtos.*;
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
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public List<BuildingResponse> findAll() {
        return buildingRepository.findAll().stream()
                .map(BuildingResponse::from)
                .collect(Collectors.toList());
    }

    public BuildingResponse findById(Long id) {
        return BuildingResponse.from(get(id));
    }

    public Building get(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found"));
    }

    public BuildingResponse create(BuildingRequest req) {
        Building b = Building.builder()
                .name(req.getName())
                .address(req.getAddress())
                .city(req.getCity())
                .postalCode(req.getPostalCode())
                .floors(req.getFloors())
                .baseRatePerSqm(req.getBaseRatePerSqm())
                .build();
        return BuildingResponse.from(buildingRepository.save(b));
    }

    public BuildingResponse update(Long id, BuildingRequest req) {
        Building b = get(id);
        b.setName(req.getName());
        b.setAddress(req.getAddress());
        b.setCity(req.getCity());
        b.setPostalCode(req.getPostalCode());
        b.setFloors(req.getFloors());
        b.setBaseRatePerSqm(req.getBaseRatePerSqm());
        return BuildingResponse.from(buildingRepository.save(b));
    }

    public void delete(Long id) {
        buildingRepository.deleteById(id);
    }
}
