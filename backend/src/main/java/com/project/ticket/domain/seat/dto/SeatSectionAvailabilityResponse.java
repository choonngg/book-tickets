package com.project.ticket.domain.seat.dto;

import java.util.List;

public record SeatSectionAvailabilityResponse(
        String section,
        long availableCount,
        List<SeatSectionSeatResponse> seats
) {
}
