package com.syndicare.domain.dtos.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardOwnerGetResponseDto {
    private BigDecimal totalDue;
    private BigDecimal totalPaid;
    private long pendingChargesCount;
    private long openTicketsCount;
    private List<Map<String, Object>> apartments;
}
