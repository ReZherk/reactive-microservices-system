package com.ReZherk.microservice_reservations.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ReZherk.microservice_reservations.entity.Reservation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReservationRepository extends ReactiveCrudRepository<Reservation, Long> {

    Flux<Reservation> findByUserId(Long userId);

    Flux<Reservation> findByStatus(String status);

    Mono<Long> countByStatus(String status);
}
