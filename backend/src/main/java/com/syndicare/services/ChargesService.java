package com.syndicare.services;

import com.syndicare.exceptions.InvalidRequestException;
import com.syndicare.mappers.ChargesGetAndPostResponseMapper;
import com.syndicare.domain.entities.buildings.Apartment;
import com.syndicare.domain.entities.buildings.Building;
import com.syndicare.domain.entities.charges.Charge;
import com.syndicare.domain.entities.charges.ChargeStatus;
import com.syndicare.domain.dtos.charges.*;
import com.syndicare.repositories.buildings.ApartmentRepository;
import com.syndicare.repositories.charges.ChargeRepository;
import com.syndicare.exceptions.ResourcesNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargesService {
    private final ChargesGetAndPostResponseMapper chargesGetAndPostResponseMapper;
    private final ChargeRepository chargeRepository;
    private final ApartmentRepository apartmentRepository;
    private final BuildingsService buildingsService;

    public List<ChargesGetAndPostResponseDto> chargesGeneratePostService(ChargesPostRequestDto dto) {
        Building building = buildingsService.getBuildingById(dto.getBuildingId());
        BigDecimal rate = dto.getRatePerSqm() != null
                ? dto.getRatePerSqm()
                : building.getBaseRatePerSqm();
        if (rate == null) {
            throw new InvalidRequestException("No rate defined for this building");
        }

        LocalDate period = dto.getPeriod().withDayOfMonth(1);
        List<Apartment> apartments = apartmentRepository.findByBuildingId(building.getId());
        List<Charge> created = new ArrayList<>();

        for (Apartment apartment : apartments) {

            List<Charge> existing = chargeRepository.findByApartmentIdAndPeriod(apartment.getId(), period);
            if (!existing.isEmpty())
                continue;

            BigDecimal amount = apartment.getSurface().multiply(rate);
            Charge charge = Charge.builder()
                    .apartment(apartment)
                    .period(period)
                    .amount(amount)
                    .description(dto.getDescription() != null
                            ? dto.getDescription()
                            : "Charges mensuelles " + period)
                    .status(ChargeStatus.PENDING)
                    .dueDate(period.plusMonths(1).minusDays(1))
                    .build();
            created.add(chargeRepository.save(charge));
        }
        return created.stream().map(chargesGetAndPostResponseMapper::map).toList();
    }

    public List<ChargesGetAndPostResponseDto> chargesGetService() {
        return chargeRepository.findAll().stream()
                .map(chargesGetAndPostResponseMapper::map)
                .toList();
    }

    public List<ChargesGetAndPostResponseDto> chargesGetByApartmentService(Long apartmentId) {
        return chargeRepository.findByApartmentId(apartmentId).stream()
                .map(chargesGetAndPostResponseMapper::map)
                .toList();
    }

    public List<ChargesGetAndPostResponseDto> chargesGetByOwnerService(Long ownerId) {
        return chargeRepository.findByApartmentOwnerId(ownerId).stream()
                .map(chargesGetAndPostResponseMapper::map)
                .toList();
    }

    public List<ChargesGetAndPostResponseDto> chargesGetByBuildingService(Long buildingId) {
        return chargeRepository.findByApartmentBuildingId(buildingId).stream()
                .map(chargesGetAndPostResponseMapper::map)
                .toList();
    }

    public ChargesGetAndPostResponseDto chargesPaymentPostService(ChargesPaymentPostRequestDto dto) {
        Charge charge = chargeRepository.findById(dto.getChargeId())
                .orElseThrow(() -> new ResourcesNotFoundException("Charge not found"));
        charge.setStatus(ChargeStatus.PAID);
        charge.setPaidAt(LocalDateTime.now());
        charge.setPaymentReference(dto.getPaymentReference());
        return chargesGetAndPostResponseMapper.map(chargeRepository.save(charge));
    }

    public Charge getChargeById(Long id) {
        return chargeRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Charge not found"));
    }

    public void chargesDeleteService(Long id) {
        chargeRepository.deleteById(id);
    }
}
