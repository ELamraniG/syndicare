package com.syndicare.domain.dtos.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAdminGetResponseDto {
    private long totalBuildings;
    private long totalApartments;
    private long totalOwners;
    private long totalResidents;
    private long pendingCharges;
    private long paidCharges;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private BigDecimal totalRevenueThisMonth;
    private BigDecimal totalUnpaidAmount;
    private List<MonthRevenueDto> revenueByMonth;
}
