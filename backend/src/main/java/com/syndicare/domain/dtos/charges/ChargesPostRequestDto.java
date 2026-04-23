package com.syndicare.domain.dtos.charges;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargesPostRequestDto {
    @NotNull
    private Long buildingId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate period;

    private BigDecimal ratePerSqm;

    private String description;
}
