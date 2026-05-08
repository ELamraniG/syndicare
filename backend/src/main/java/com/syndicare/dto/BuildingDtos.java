package com.syndicare.dto;

import com.syndicare.domain.Building;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

public class BuildingDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingRequest {
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BuildingResponse {
        private Long id;
        private String name;
        private String address;
        private String city;
        private String postalCode;
        private Integer floors;
        private BigDecimal baseRatePerSqm;
        private int apartmentCount;

        public static BuildingResponse from(Building b) {
            return BuildingResponse.builder()
                    .id(b.getId())
                    .name(b.getName())
                    .address(b.getAddress())
                    .city(b.getCity())
                    .postalCode(b.getPostalCode())
                    .floors(b.getFloors())
                    .baseRatePerSqm(b.getBaseRatePerSqm())
                    .apartmentCount(b.getApartments() == null ? 0 : b.getApartments().size())
                    .build();
        }
    }
}
