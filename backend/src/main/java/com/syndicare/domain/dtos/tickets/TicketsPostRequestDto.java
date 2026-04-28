package com.syndicare.domain.dtos.tickets;

import com.syndicare.domain.entities.tickets.TicketCategory;
import com.syndicare.domain.entities.tickets.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketsPostRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private TicketCategory category;
    private TicketPriority priority;
    private Long apartmentId;
    private Long buildingId;
}
