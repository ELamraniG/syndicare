package com.syndicare.domain.dtos.buildings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingsPostAndPutRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    private String city;
    private String postalCode;
    @NotNull @Positive
    private Integer floors;
    private BigDecimal baseRatePerSqm;
}
