package com.syndicare.domain.dtos.tickets;

import com.syndicare.domain.entities.tickets.TicketPriority;
import com.syndicare.domain.entities.tickets.TicketStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketsPatchRequestDto {
    private TicketStatus status;
    private TicketPriority priority;
    private String adminNotes;
}
