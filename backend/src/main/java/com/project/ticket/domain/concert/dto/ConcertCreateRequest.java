package com.project.ticket.domain.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record ConcertCreateRequest(
        @NotBlank String title,
        @NotBlank String venue,
        @NotNull LocalDateTime concertDate,
        @NotNull LocalDateTime ticketOpenDate,
        @NotNull LocalDateTime ticketCloseDate,
        @PositiveOrZero int price
) {
}
