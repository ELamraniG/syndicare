package com.syndicare.dto;

import com.syndicare.domain.Apartment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

public class ApartmentDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApartmentRequest {
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApartmentResponse {
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

        public static ApartmentResponse from(Apartment a) {
            return ApartmentResponse.builder()
                    .id(a.getId())
                    .number(a.getNumber())
                    .floor(a.getFloor())
                    .surface(a.getSurface())
                    .buildingId(a.getBuilding() != null ? a.getBuilding().getId() : null)
                    .buildingName(a.getBuilding() != null ? a.getBuilding().getName() : null)
                    .ownerId(a.getOwner() != null ? a.getOwner().getId() : null)
                    .ownerName(a.getOwner() != null ? a.getOwner().getFirstName() + " " + a.getOwner().getLastName() : null)
                    .residentId(a.getResident() != null ? a.getResident().getId() : null)
                    .residentName(a.getResident() != null ? a.getResident().getFirstName() + " " + a.getResident().getLastName() : null)
                    .build();
        }
    }
}
