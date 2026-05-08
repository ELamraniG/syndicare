package com.syndicare.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdminDashboardResponse {
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
        private List<MonthRevenue> revenueByMonth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthRevenue {
        private String month;       // "2026-05"
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwnerDashboardResponse {
        private BigDecimal totalDue;
        private BigDecimal totalPaid;
        private long pendingChargesCount;
        private long openTicketsCount;
        private List<Map<String, Object>> apartments;
    }
}
