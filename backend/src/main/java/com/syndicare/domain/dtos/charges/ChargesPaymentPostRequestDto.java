package com.syndicare.domain.dtos.charges;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargesPaymentPostRequestDto {
    @NotNull
    private Long chargeId;
    private String paymentReference;
}
