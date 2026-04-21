package com.syndicare.mappers;

import com.syndicare.domain.entities.users.User;
import com.syndicare.domain.dtos.users.UsersGetResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersGetResponseMapper extends StandardMapper<User, UsersGetResponseDto> {}
