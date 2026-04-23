package com.syndicare.services;

import com.syndicare.domain.dtos.apartments.ApartmentsGetAndPostResponseDto;
import com.syndicare.domain.dtos.apartments.ApartmentsPostAndPutRequestDto;
import com.syndicare.domain.entities.buildings.Apartment;
import com.syndicare.domain.entities.buildings.Building;
import com.syndicare.domain.entities.users.User;
import com.syndicare.exceptions.ResourcesNotFoundException;
import com.syndicare.repositories.buildings.ApartmentRepository;
import com.syndicare.repositories.buildings.BuildingRepository;
import com.syndicare.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentsService {
    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;

    public List<ApartmentsGetAndPostResponseDto> apartmentsGetService() {
        return mapList(apartmentRepository.findAll());
    }

    public List<ApartmentsGetAndPostResponseDto> apartmentsGetByBuildingService(Long buildingId) {
        return mapList(apartmentRepository.findByBuildingId(buildingId));
    }

    public List<ApartmentsGetAndPostResponseDto> apartmentsGetByOwnerService(Long ownerId) {
        return mapList(apartmentRepository.findByOwnerId(ownerId));
    }

    public List<ApartmentsGetAndPostResponseDto> apartmentsGetByResidentService(Long residentId) {
        return mapList(apartmentRepository.findByResidentId(residentId));
    }

    public ApartmentsGetAndPostResponseDto apartmentsGetIdService(Long id) {
        return toDto(getApartmentById(id));
    }

    public Apartment getApartmentById(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Apartment not found"));
    }

    public ApartmentsGetAndPostResponseDto apartmentsPostService(ApartmentsPostAndPutRequestDto dto) {
        Apartment apartment = new Apartment();
        setApartmentValues(apartment, dto);
        apartmentRepository.save(apartment);
        return toDto(apartment);
    }

    public ApartmentsGetAndPostResponseDto apartmentsPutService(Long id, ApartmentsPostAndPutRequestDto dto) {
        Apartment apartment = getApartmentById(id);
        setApartmentValues(apartment, dto);
        return toDto(apartmentRepository.save(apartment));
    }

    public void apartmentsDeleteService(Long id) {
        apartmentRepository.deleteById(id);
    }

    private void setApartmentValues(Apartment apartment, ApartmentsPostAndPutRequestDto dto) {
        apartment.setNumber(dto.getNumber());
        apartment.setFloor(dto.getFloor());
        apartment.setSurface(dto.getSurface());

        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new ResourcesNotFoundException("Building not found"));
        apartment.setBuilding(building);

        if (dto.getOwnerId() != null) {
            User owner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new ResourcesNotFoundException("Owner not found"));
            apartment.setOwner(owner);
        } else apartment.setOwner(null);

        if (dto.getResidentId() != null) {
            User resident = userRepository.findById(dto.getResidentId())
                    .orElseThrow(() -> new ResourcesNotFoundException("Resident not found"));
            apartment.setResident(resident);
        }
    }

    private List<ApartmentsGetAndPostResponseDto> mapList(List<Apartment> apartments) {
        List<ApartmentsGetAndPostResponseDto> result = new ArrayList<>();
        for (Apartment apartment : apartments)
            result.add(toDto(apartment));
        return result;
    }

    private ApartmentsGetAndPostResponseDto toDto(Apartment apartment) {
        ApartmentsGetAndPostResponseDto dto = new ApartmentsGetAndPostResponseDto();
        dto.setId(apartment.getId());
        dto.setNumber(apartment.getNumber());
        dto.setFloor(apartment.getFloor());
        dto.setSurface(apartment.getSurface());

        if (apartment.getBuilding() != null) {
            dto.setBuildingId(apartment.getBuilding().getId());
            dto.setBuildingName(apartment.getBuilding().getName());
        }
        if (apartment.getOwner() != null) {
            dto.setOwnerId(apartment.getOwner().getId());
            dto.setOwnerName(apartment.getOwner().getFirstName() + " " + apartment.getOwner().getLastName());
        }
        if (apartment.getResident() != null) {
            dto.setResidentId(apartment.getResident().getId());
            dto.setResidentName(apartment.getResident().getFirstName() + " " + apartment.getResident().getLastName());
        }
        return dto;
    }
}
