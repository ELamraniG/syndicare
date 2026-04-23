package com.syndicare.domain.dtos.charges;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syndicare.domain.entities.charges.ChargeStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargesGetAndPostResponseDto {
    private Long id;
    private Long apartmentId;
    private String apartmentNumber;
    private String buildingName;
    private String ownerName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate period;
    private BigDecimal amount;
    private String description;
    private ChargeStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private LocalDateTime paidAt;
    private String paymentReference;
}
