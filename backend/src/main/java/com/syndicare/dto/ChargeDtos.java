package com.syndicare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syndicare.domain.Charge;
import com.syndicare.domain.ChargeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChargeDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateChargesRequest {
        @NotNull
        private Long buildingId;

        /** First day of the period (e.g. 2026-05-01). */
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate period;

        /** Optional override of the rate per sqm; otherwise uses building.baseRatePerSqm. */
        private BigDecimal ratePerSqm;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidatePaymentRequest {
        @NotNull
        private Long chargeId;
        private String paymentReference;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargeResponse {
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

        public static ChargeResponse from(Charge c) {
            return ChargeResponse.builder()
                    .id(c.getId())
                    .apartmentId(c.getApartment().getId())
                    .apartmentNumber(c.getApartment().getNumber())
                    .buildingName(c.getApartment().getBuilding() != null
                            ? c.getApartment().getBuilding().getName() : null)
                    .ownerName(c.getApartment().getOwner() != null
                            ? c.getApartment().getOwner().getFirstName() + " " + c.getApartment().getOwner().getLastName()
                            : null)
                    .period(c.getPeriod())
                    .amount(c.getAmount())
                    .description(c.getDescription())
                    .status(c.getStatus())
                    .dueDate(c.getDueDate())
                    .paidAt(c.getPaidAt())
                    .paymentReference(c.getPaymentReference())
                    .build();
        }
    }
}
