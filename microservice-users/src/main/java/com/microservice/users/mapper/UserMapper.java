package com.microservice.users.mapper;

import org.springframework.stereotype.Component;

import com.ReZherk.common.dto.request.UserRequestDto;
import com.ReZherk.common.dto.response.UserResponseDto;

import com.microservice.users.dto.request.UserUpdateDto;
import com.microservice.users.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto dto) {

        User entity = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        return entity;
    }

    public UserResponseDto toDto(User entity) {
        return UserResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }

    public void updateEntityFromDto(UserUpdateDto dto, User entity) {
        if (dto.getFirstName() != null)
            entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null)
            entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null)
            entity.setPhone(dto.getPhone());
        if (dto.getAddress() != null)
            entity.setAddress(dto.getAddress());
        if (dto.getStatus() != null)
            entity.setStatus(dto.getStatus());
    }
}
