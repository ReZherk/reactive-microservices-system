package com.ReZherk.microservice_reservations.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateDto {

    @NotBlank(message = "Resource name is required")
    private String resourceName;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be greater than 0")
    private Double totalPrice;
}
