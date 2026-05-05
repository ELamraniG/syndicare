package com.syndicare.domain.dtos.dashboard;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthRevenueDto {
    private String month;
    private BigDecimal amount;
}
