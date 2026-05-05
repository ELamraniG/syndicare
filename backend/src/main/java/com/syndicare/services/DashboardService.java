package com.syndicare.services;

import com.syndicare.domain.entities.buildings.Apartment;
import com.syndicare.domain.entities.charges.Charge;
import com.syndicare.domain.entities.charges.ChargeStatus;
import com.syndicare.domain.entities.tickets.TicketStatus;
import com.syndicare.domain.entities.users.Role;
import com.syndicare.domain.dtos.dashboard.*;
import com.syndicare.repositories.buildings.ApartmentRepository;
import com.syndicare.repositories.buildings.BuildingRepository;
import com.syndicare.repositories.charges.ChargeRepository;
import com.syndicare.repositories.tickets.TicketRepository;
import com.syndicare.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final ChargeRepository chargeRepository;
    private final TicketRepository ticketRepository;

    public DashboardAdminGetResponseDto dashboardAdminGetService() {
        List<Charge> allCharges = chargeRepository.findAll();
        LocalDate now = LocalDate.now().withDayOfMonth(1);

        BigDecimal revenueThisMonth = allCharges.stream()
                .filter(charge -> charge.getStatus() == ChargeStatus.PAID && charge.getPeriod().equals(now))
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnpaid = allCharges.stream()
                .filter(charge -> charge.getStatus() != ChargeStatus.PAID)
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now).minusMonths(i);
            revenueByMonth.put(yearMonth.toString(), BigDecimal.ZERO);
        }
        for (Charge charge : allCharges) {
            if (charge.getStatus() != ChargeStatus.PAID)
                continue;
            String key = YearMonth.from(charge.getPeriod()).toString();
            if (revenueByMonth.containsKey(key)) {
                revenueByMonth.merge(key, charge.getAmount(), BigDecimal::add);
            }
        }
        List<MonthRevenueDto> series = revenueByMonth.entrySet().stream()
                .map(entry -> MonthRevenueDto.builder().month(entry.getKey()).amount(entry.getValue()).build())
                .toList();

        return DashboardAdminGetResponseDto.builder()
                .totalBuildings(buildingRepository.count())
                .totalApartments(apartmentRepository.count())
                .totalOwners(userRepository.findByRole(Role.OWNER).size())
                .totalResidents(userRepository.findByRole(Role.RESIDENT).size())
                .pendingCharges(chargeRepository.countByStatus(ChargeStatus.PENDING))
                .paidCharges(chargeRepository.countByStatus(ChargeStatus.PAID))
                .openTickets(ticketRepository.countByStatus(TicketStatus.OPEN))
                .inProgressTickets(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS))
                .resolvedTickets(ticketRepository.countByStatus(TicketStatus.RESOLVED))
                .totalRevenueThisMonth(revenueThisMonth)
                .totalUnpaidAmount(totalUnpaid)
                .revenueByMonth(series)
                .build();
    }

    public DashboardOwnerGetResponseDto dashboardOwnerGetService(Long ownerId) {
        List<Charge> charges = chargeRepository.findByApartmentOwnerId(ownerId);
        BigDecimal totalDue = charges.stream()
                .filter(charge -> charge.getStatus() != ChargeStatus.PAID)
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = charges.stream()
                .filter(charge -> charge.getStatus() == ChargeStatus.PAID)
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pending = charges.stream()
                .filter(charge -> charge.getStatus() == ChargeStatus.PENDING)
                .count();

        List<Apartment> apartments = apartmentRepository.findByOwnerId(ownerId);
        List<Map<String, Object>> apartmentSummaries = apartments.stream().map(apartment -> {
            Map<String, Object> apartmentSummary = new HashMap<>();
            apartmentSummary.put("id", apartment.getId());
            apartmentSummary.put("number", apartment.getNumber());
            apartmentSummary.put("buildingName", apartment.getBuilding() != null ? apartment.getBuilding().getName() : null);
            apartmentSummary.put("surface", apartment.getSurface());
            return apartmentSummary;
        }).toList();

        return DashboardOwnerGetResponseDto.builder()
                .totalDue(totalDue)
                .totalPaid(totalPaid)
                .pendingChargesCount(pending)
                .openTicketsCount(0L)
                .apartments(apartmentSummaries)
                .build();
    }
}
