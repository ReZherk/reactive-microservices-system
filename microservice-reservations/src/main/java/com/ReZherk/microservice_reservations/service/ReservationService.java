package com.ReZherk.microservice_reservations.service;

import org.springframework.stereotype.Service;

import com.ReZherk.microservice_reservations.client.UserServiceClient;
import com.ReZherk.microservice_reservations.dto.request.ReservationRequestDto;
import com.ReZherk.microservice_reservations.dto.response.ReservationResponseDto;
import com.ReZherk.microservice_reservations.entity.Reservation;
import com.ReZherk.microservice_reservations.exception.InvalidReservationException;
import com.ReZherk.microservice_reservations.mapper.ReservationMapper;
import com.ReZherk.microservice_reservations.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserServiceClient userServiceClient;
    private final ReservationMapper reservationMapper;

    public Mono<ReservationResponseDto> createReservation(ReservationRequestDto requestDto) {
        if (requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
            return Mono.error(new InvalidReservationException("Start date must be  before end date"));
        }

        return userServiceClient.validateUser(requestDto.getUserId())
                .flatMap(isValid -> {

                    if (!isValid) {
                        return Mono.error(new InvalidReservationException("User is not active or does not exist"));
                    }

                    Reservation reservation = reservationMapper.toEntity(requestDto);

                    return reservationRepository.save(reservation)
                            .flatMap(saved -> userServiceClient.getUserById(requestDto.getUserId())
                                    .map(user -> reservationMapper.toDto(saved, user))
                                    .onErrorReturn(reservationMapper.toDto(saved, null)));

                });
    }
}
