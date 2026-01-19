package com.ReZherk.microservice_reservations.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ReZherk.microservice_reservations.client.UserServiceClient;
import com.ReZherk.microservice_reservations.dto.request.ReservationRequestDto;
import com.ReZherk.microservice_reservations.dto.request.ReservationUpdateDto;
import com.ReZherk.microservice_reservations.dto.response.ReservationResponseDto;
import com.ReZherk.microservice_reservations.entity.Reservation;
import com.ReZherk.microservice_reservations.exception.InvalidReservationException;
import com.ReZherk.microservice_reservations.exception.ReservationNotFoundException;
import com.ReZherk.microservice_reservations.mapper.ReservationMapper;
import com.ReZherk.microservice_reservations.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserServiceClient userServiceClient;
    private final ReservationMapper reservationMapper;

    public Flux<LocalDateTime> getAvailableDates(String resourceName, LocalDateTime start, LocalDateTime end) {

        return reservationRepository.findByResourceNameAndDateRange(resourceName, start, end)
                .map(reservation -> reservation.getStartDate())
                .collectList()
                .flatMapMany(reservedDates -> {

                    List<LocalDateTime> allDates = new ArrayList<>();
                    LocalDateTime current = start;
                    while (current.isBefore(end)) {

                        allDates.add(current);
                        current = current.plusDays(1);
                    }

                    List<LocalDateTime> available = allDates.stream()
                            .filter(date -> !reservedDates.contains(date))
                            .collect(Collectors.toList());

                    return Flux.fromIterable(available);

                });
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredReservations() {
        reservationRepository.findByStatus("PENDING")
                .filter(reservation -> reservation.getCreatedAt() != null
                        && reservation.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1)))
                .flatMap(reservation -> {
                    reservation.setStatus("CANCELLED");
                    reservation.setUpdatedAt(LocalDateTime.now());
                    return reservationRepository.save(reservation);
                })
                .subscribe();
    }

    public Mono<ReservationResponseDto> createReservation(ReservationRequestDto requestDto) {
        if (requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
            return Mono.error(new InvalidReservationException("Start date must be before end date"));
        }

        List<LocalDateTime> requestedDates = new ArrayList<>();
        LocalDateTime current = requestDto.getStartDate();
        while (!current.isAfter(requestDto.getEndDate())) {
            requestedDates.add(current);
            current = current.plusDays(1);
        }

        return getAvailableDates(requestDto.getResourceName(), requestDto.getStartDate(), requestDto.getEndDate())
                .collectList()
                .flatMap(availableDates -> {
                    if (!availableDates.containsAll(requestedDates)) {
                        return Mono.error(new InvalidReservationException("Some requested dates are not available"));
                    }

                    return userServiceClient.validateUser(requestDto.getUserId())
                            .flatMap(isValid -> {
                                if (!isValid) {
                                    return Mono.error(
                                            new InvalidReservationException("User is not active or does not exist"));
                                }

                                Reservation reservation = reservationMapper.toEntity(requestDto);

                                return reservationRepository.save(reservation)
                                        .flatMap(saved -> userServiceClient.getUserById(requestDto.getUserId())
                                                .map(user -> reservationMapper.toDto(saved, user))
                                                .onErrorReturn(reservationMapper.toDto(saved, null)));
                            });
                });
    }

    public Mono<ReservationResponseDto> getReservationById(Long id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation with  id" + id + " not found")))
                .flatMap(reservation -> userServiceClient.getUserById(reservation.getUserId())
                        .map(user -> reservationMapper.toDto(reservation, user))
                        .onErrorReturn(reservationMapper.toDto(reservation, null)));
    }

    public Flux<ReservationResponseDto> getAllReservations() {

        return reservationRepository.findAll()
                .flatMap(reservation -> userServiceClient.getUserById(reservation.getUserId())
                        .map(user -> reservationMapper.toDto(reservation, user))
                        .onErrorReturn(reservationMapper.toDto(reservation, null)));
    }

    public Flux<ReservationResponseDto> getReservationsByUserId(Long userId) {

        return reservationRepository.findByUserId(userId)
                .flatMap(reservation -> userServiceClient.getUserById(reservation.getUserId())
                        .map(user -> reservationMapper.toDto(reservation, user))

                        .onErrorReturn(reservationMapper.toDto(reservation, null)));
    }

    public Flux<ReservationResponseDto> getReservationsByStatus(String status) {

        return reservationRepository.findByStatus(status)
                .flatMap(reservation -> userServiceClient.getUserById(reservation.getUserId())
                        .map(user -> reservationMapper.toDto(reservation, user))
                        .onErrorReturn(reservationMapper.toDto(reservation, null)));
    }

    public Mono<ReservationResponseDto> updateReservation(Long id, ReservationUpdateDto updateDto) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation with id " + id + " not found")))
                .flatMap(reservation -> {
                    if (updateDto.getStartDate() != null && updateDto.getEndDate() != null) {
                        if (updateDto.getStartDate().isAfter(updateDto.getEndDate())) {
                            return Mono.error(new InvalidReservationException("Start date must be before end date"));
                        }
                    }

                    reservationMapper.updateEntityFromDto(updateDto, reservation);
                    return reservationRepository.save(reservation)
                            .flatMap(updated -> userServiceClient.getUserById(updated.getUserId())
                                    .map(user -> reservationMapper.toDto(updated, user))
                                    .onErrorReturn(reservationMapper.toDto(updated, null)));
                });
    }

    public Mono<Void> deleteReservation(Long id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation with id " + id + " not found")))
                .flatMap(reservation -> reservationRepository.deleteById(id));
    }

    public Mono<Long> countReservationsByStatus(String status) {
        return reservationRepository.countByStatus(status);
    }

    public Mono<List<ReservationResponseDto>> getActiveReservationsWithStreams() {
        return reservationRepository.findByStatus("CONFIRMED")
                .collectList()
                .map(reservations -> reservations.stream()
                        .filter(r -> r.getStartDate().isAfter(LocalDateTime.now()))
                        .sorted((r1, r2) -> r1.getStartDate().compareTo(r2.getStartDate()))
                        .map(r -> reservationMapper.toDto(r, null))
                        .collect(Collectors.toList()));
    }

    public Mono<Double> calculateTotalRevenue() {
        return reservationRepository.findByStatus("CONFIRMED")
                .collectList()
                .map(reservations -> reservations.stream()
                        .filter(r -> r.getTotalPrice() != null)
                        .mapToDouble(Reservation::getTotalPrice)
                        .sum());
    }

    public Mono<List<String>> getResourceNamesWithMinPrice(Double minPrice) {
        return reservationRepository.findAll()
                .collectList()
                .map(reservations -> reservations.stream()
                        .filter(r -> r.getTotalPrice() != null && r.getTotalPrice() >= minPrice)
                        .map(Reservation::getResourceName)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList()));
    }

    public Mono<String> getReservationsSummary() {
        return reservationRepository.findAll()
                .collectList()
                .map(reservations -> {
                    long total = reservations.size();
                    long confirmed = reservations.stream()
                            .filter(r -> "CONFIRMED".equals(r.getStatus()))
                            .count();
                    long pending = reservations.stream()
                            .filter(r -> "PENDING".equals(r.getStatus()))
                            .count();

                    return String.format("Total: %d, Confirmed: %d, Pending: %d", total, confirmed, pending);
                });
    }

}
