package com.syndicare.services;

import com.syndicare.domain.dtos.buildings.BuildingsGetAndPostResponseDto;
import com.syndicare.domain.dtos.buildings.BuildingsPostAndPutRequestDto;
import com.syndicare.domain.entities.buildings.Building;
import com.syndicare.exceptions.ResourcesNotFoundException;
import com.syndicare.repositories.buildings.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingsService {
    private final BuildingRepository buildingRepository;

    public List<BuildingsGetAndPostResponseDto> buildingsGetService() {
        List<BuildingsGetAndPostResponseDto> result = new ArrayList<>();
        for (Building building : buildingRepository.findAll()) {
            result.add(toDto(building));
        }
        return result;
    }

    public BuildingsGetAndPostResponseDto buildingsGetIdService(Long id) {
        return toDto(getBuildingById(id));
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Building not found"));
    }

    public BuildingsGetAndPostResponseDto buildingsPostService(BuildingsPostAndPutRequestDto dto) {
        Building building = new Building();
        building.setName(dto.getName());
        building.setAddress(dto.getAddress());
        building.setCity(dto.getCity());
        building.setPostalCode(dto.getPostalCode());
        building.setFloors(dto.getFloors());
        building.setBaseRatePerSqm(dto.getBaseRatePerSqm());
        return toDto(buildingRepository.save(building));
    }

    public BuildingsGetAndPostResponseDto buildingsPutService(Long id, BuildingsPostAndPutRequestDto dto) {
        Building building = getBuildingById(id);
        building.setName(dto.getName());
        building.setAddress(dto.getAddress());
        building.setCity(dto.getCity());
        building.setPostalCode(dto.getPostalCode());
        building.setFloors(dto.getFloors());
        building.setBaseRatePerSqm(dto.getBaseRatePerSqm());
        buildingRepository.save(building);
        return toDto(building);
    }

    public void buildingsDeleteService(Long id) {
        if (!buildingRepository.existsById(id))
            throw new ResourcesNotFoundException("Building not found");
        buildingRepository.deleteById(id);
    }

    private BuildingsGetAndPostResponseDto toDto(Building building) {
        BuildingsGetAndPostResponseDto dto = new BuildingsGetAndPostResponseDto();
        dto.setId(building.getId());
        dto.setName(building.getName());
        dto.setAddress(building.getAddress());
        dto.setCity(building.getCity());
        dto.setPostalCode(building.getPostalCode());
        dto.setFloors(building.getFloors());
        dto.setBaseRatePerSqm(building.getBaseRatePerSqm());
        dto.setApartmentCount(building.getApartments() == null ? 0 : building.getApartments().size());
        return dto;
    }
}
