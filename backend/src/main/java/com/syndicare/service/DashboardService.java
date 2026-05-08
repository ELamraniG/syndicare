package com.syndicare.service;

import com.syndicare.domain.*;
import com.syndicare.dto.DashboardDtos.*;
import com.syndicare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final ChargeRepository chargeRepository;
    private final TicketRepository ticketRepository;

    public AdminDashboardResponse adminStats() {
        List<Charge> allCharges = chargeRepository.findAll();
        LocalDate now = LocalDate.now().withDayOfMonth(1);

        BigDecimal revenueThisMonth = allCharges.stream()
                .filter(c -> c.getStatus() == ChargeStatus.PAID && c.getPeriod().equals(now))
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnpaid = allCharges.stream()
                .filter(c -> c.getStatus() != ChargeStatus.PAID)
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Last 6 months revenue
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.from(now).minusMonths(i);
            revenueByMonth.put(ym.toString(), BigDecimal.ZERO);
        }
        for (Charge c : allCharges) {
            if (c.getStatus() != ChargeStatus.PAID) continue;
            String key = YearMonth.from(c.getPeriod()).toString();
            if (revenueByMonth.containsKey(key)) {
                revenueByMonth.merge(key, c.getAmount(), BigDecimal::add);
            }
        }
        List<MonthRevenue> series = revenueByMonth.entrySet().stream()
                .map(e -> MonthRevenue.builder().month(e.getKey()).amount(e.getValue()).build())
                .collect(Collectors.toList());

        return AdminDashboardResponse.builder()
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

    public OwnerDashboardResponse ownerStats(Long ownerId) {
        List<Charge> charges = chargeRepository.findByApartmentOwnerId(ownerId);
        BigDecimal totalDue = charges.stream()
                .filter(c -> c.getStatus() != ChargeStatus.PAID)
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = charges.stream()
                .filter(c -> c.getStatus() == ChargeStatus.PAID)
                .map(Charge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pending = charges.stream()
                .filter(c -> c.getStatus() == ChargeStatus.PENDING)
                .count();

        List<Apartment> apts = apartmentRepository.findByOwnerId(ownerId);
        List<Map<String, Object>> aptSummaries = apts.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("number", a.getNumber());
            m.put("buildingName", a.getBuilding() != null ? a.getBuilding().getName() : null);
            m.put("surface", a.getSurface());
            return m;
        }).collect(Collectors.toList());

        return OwnerDashboardResponse.builder()
                .totalDue(totalDue)
                .totalPaid(totalPaid)
                .pendingChargesCount(pending)
                .openTicketsCount(0L)
                .apartments(aptSummaries)
                .build();
    }
}
