package com.syndicare.domain.dtos.apartments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentsPostAndPutRequestDto {
    @NotBlank
    private String number;
    @NotNull
    private Integer floor;
    @NotNull @Positive
    private BigDecimal surface;
    @NotNull
    private Long buildingId;
    private Long ownerId;
    private Long residentId;
}
