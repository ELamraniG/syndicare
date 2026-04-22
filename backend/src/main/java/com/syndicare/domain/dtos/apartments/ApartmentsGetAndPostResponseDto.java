package com.syndicare.domain.dtos.apartments;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentsGetAndPostResponseDto {
    private Long id;
    private String number;
    private Integer floor;
    private BigDecimal surface;
    private Long buildingId;
    private String buildingName;
    private Long ownerId;
    private String ownerName;
    private Long residentId;
    private String residentName;
}
