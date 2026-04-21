package com.syndicare.domain.dtos.buildings;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingsGetAndPostResponseDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private Integer floors;
    private BigDecimal baseRatePerSqm;
    private int apartmentCount;
}
