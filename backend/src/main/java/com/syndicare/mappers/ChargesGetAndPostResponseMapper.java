package com.syndicare.mappers;

import com.syndicare.domain.entities.charges.Charge;
import com.syndicare.domain.dtos.charges.ChargesGetAndPostResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.InheritConfiguration;

@Mapper(componentModel = "spring")
public interface ChargesGetAndPostResponseMapper extends StandardMapper<Charge, ChargesGetAndPostResponseDto> {
    @Mapping(target = "apartmentId", expression = "java(charge.getApartment().getId())")
    @Mapping(target = "apartmentNumber", expression = "java(charge.getApartment().getNumber())")
    @Mapping(target = "buildingName", expression = "java(charge.getApartment().getBuilding() != null ? charge.getApartment().getBuilding().getName() : null)")
    @Mapping(target = "ownerName", expression = "java(charge.getApartment().getOwner() != null ? charge.getApartment().getOwner().getFirstName() + \" \" + charge.getApartment().getOwner().getLastName() : null)")
    ChargesGetAndPostResponseDto map(Charge charge);

    @InheritConfiguration(name = "map")
    ChargesGetAndPostResponseDto update(Charge charge, @MappingTarget ChargesGetAndPostResponseDto dto);
}
