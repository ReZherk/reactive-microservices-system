package com.ReZherk.microservice_reservations.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.ReZherk.microservice_reservations.dto.request.ReservationRequestDto;
import com.ReZherk.microservice_reservations.dto.request.ReservationUpdateDto;
import com.ReZherk.microservice_reservations.dto.response.ReservationResponseDto;
import com.ReZherk.microservice_reservations.dto.response.UserDto;
import com.ReZherk.microservice_reservations.entity.Reservation;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequestDto dto) {
        return Reservation.builder()
                .userId(dto.getUserId())
                .resourceName(dto.getResourceName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalPrice(dto.getTotalPrice())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public ReservationResponseDto toDto(Reservation entity, UserDto user) {

        String userName = user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";

        Long userId = user != null ? user.getId() : null;

        return ReservationResponseDto.builder()
                .id(entity.getId())
                .userId(userId)
                .userName(userName)
                .resourceName(entity.getResourceName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .totalPrice(entity.getTotalPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(ReservationUpdateDto dto, Reservation entity) {
        if (dto.getResourceName() != null)
            entity.setResourceName(dto.getResourceName());
        if (dto.getStartDate() != null)
            entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)
            entity.setEndDate(dto.getEndDate());
        if (dto.getStatus() != null)
            entity.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null)
            entity.setTotalPrice(dto.getTotalPrice());
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
