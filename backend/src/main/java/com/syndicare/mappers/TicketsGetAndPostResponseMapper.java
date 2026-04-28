package com.syndicare.mappers;

import com.syndicare.domain.entities.tickets.Ticket;
import com.syndicare.domain.dtos.tickets.TicketsGetAndPostResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.InheritConfiguration;

@Mapper(componentModel = "spring")
public interface TicketsGetAndPostResponseMapper extends StandardMapper<Ticket, TicketsGetAndPostResponseDto> {
    @Mapping(target = "submitterId", expression = "java(ticket.getSubmitter().getId())")
    @Mapping(target = "submitterName", expression = "java(ticket.getSubmitter().getFirstName() + \" \" + ticket.getSubmitter().getLastName())")
    @Mapping(target = "apartmentId", expression = "java(ticket.getApartment() != null ? ticket.getApartment().getId() : null)")
    @Mapping(target = "apartmentNumber", expression = "java(ticket.getApartment() != null ? ticket.getApartment().getNumber() : null)")
    @Mapping(target = "buildingId", expression = "java(ticket.getBuilding() != null ? ticket.getBuilding().getId() : null)")
    @Mapping(target = "buildingName", expression = "java(ticket.getBuilding() != null ? ticket.getBuilding().getName() : null)")
    TicketsGetAndPostResponseDto map(Ticket ticket);

    @InheritConfiguration(name = "map")
    TicketsGetAndPostResponseDto update(Ticket ticket, @MappingTarget TicketsGetAndPostResponseDto dto);
}
