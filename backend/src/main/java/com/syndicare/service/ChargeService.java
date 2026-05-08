package com.syndicare.service;

import com.syndicare.domain.*;
import com.syndicare.dto.ChargeDtos.*;
import com.syndicare.repository.ApartmentRepository;
import com.syndicare.repository.ChargeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargeService {

    private final ChargeRepository chargeRepository;
    private final ApartmentRepository apartmentRepository;
    private final BuildingService buildingService;

    /**
     * Generate monthly charges for every apartment in a building.
     * Amount = surface (m²) × ratePerSqm.
     */
    public List<ChargeResponse> generateForBuilding(GenerateChargesRequest req) {
        Building b = buildingService.get(req.getBuildingId());
        BigDecimal rate = req.getRatePerSqm() != null
                ? req.getRatePerSqm()
                : b.getBaseRatePerSqm();
        if (rate == null) {
            throw new IllegalArgumentException("No rate defined for this building");
        }

        // Period normalised to first day of month
        LocalDate period = req.getPeriod().withDayOfMonth(1);
        List<Apartment> apartments = apartmentRepository.findByBuildingId(b.getId());
        List<Charge> created = new ArrayList<>();

        for (Apartment apt : apartments) {
            // Skip if charge already exists for this apartment+period
            List<Charge> existing = chargeRepository.findByApartmentIdAndPeriod(apt.getId(), period);
            if (!existing.isEmpty()) continue;

            BigDecimal amount = apt.getSurface().multiply(rate).setScale(2, RoundingMode.HALF_UP);
            Charge c = Charge.builder()
                    .apartment(apt)
                    .period(period)
                    .amount(amount)
                    .description(req.getDescription() != null
                            ? req.getDescription()
                            : "Charges mensuelles " + period)
                    .status(ChargeStatus.PENDING)
                    .dueDate(period.plusMonths(1).minusDays(1))
                    .build();
            created.add(chargeRepository.save(c));
        }
        return created.stream().map(ChargeResponse::from).collect(Collectors.toList());
    }

    public List<ChargeResponse> findAll() {
        return chargeRepository.findAll().stream()
                .map(ChargeResponse::from)
                .collect(Collectors.toList());
    }

    public List<ChargeResponse> findByApartment(Long apartmentId) {
        return chargeRepository.findByApartmentId(apartmentId).stream()
                .map(ChargeResponse::from)
                .collect(Collectors.toList());
    }

    public List<ChargeResponse> findByOwner(Long ownerId) {
        return chargeRepository.findByApartmentOwnerId(ownerId).stream()
                .map(ChargeResponse::from)
                .collect(Collectors.toList());
    }

    public List<ChargeResponse> findByBuilding(Long buildingId) {
        return chargeRepository.findByApartmentBuildingId(buildingId).stream()
                .map(ChargeResponse::from)
                .collect(Collectors.toList());
    }

    public ChargeResponse validatePayment(ValidatePaymentRequest req) {
        Charge c = chargeRepository.findById(req.getChargeId())
                .orElseThrow(() -> new EntityNotFoundException("Charge not found"));
        c.setStatus(ChargeStatus.PAID);
        c.setPaidAt(LocalDateTime.now());
        c.setPaymentReference(req.getPaymentReference());
        return ChargeResponse.from(chargeRepository.save(c));
    }

    public Charge get(Long id) {
        return chargeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charge not found"));
    }

    public void delete(Long id) {
        chargeRepository.deleteById(id);
    }
}
