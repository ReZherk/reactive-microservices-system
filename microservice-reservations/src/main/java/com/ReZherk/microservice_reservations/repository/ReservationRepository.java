package com.ReZherk.microservice_reservations.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT * FROM reservations r " +
            "WHERE r.resource_name = :resourceName " +
            "AND r.start_date <= :end " +
            "AND r.end_date >= :start")
    Flux<Reservation> findByResourceNameAndDateRange(
            @Param("resourceName") String resourceName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
