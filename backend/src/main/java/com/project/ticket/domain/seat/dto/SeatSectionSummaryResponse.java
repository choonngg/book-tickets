package com.project.ticket.domain.seat.dto;

public record SeatSectionSummaryResponse(
        String section,
        long availableCount
) {
}
