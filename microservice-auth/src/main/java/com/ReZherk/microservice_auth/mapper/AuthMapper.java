package com.ReZherk.microservice_auth.mapper;

import org.springframework.stereotype.Component;

import com.ReZherk.common.dto.request.UserRequestDto;
import com.ReZherk.microservice_auth.dto.RegisterRequestDto;

@Component
public class AuthMapper {

    public UserRequestDto mapToUserRequestDto(RegisterRequestDto request) {
        return UserRequestDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(request.getStatus())
                .address(request.getAddress())
                .build();

    }
}
