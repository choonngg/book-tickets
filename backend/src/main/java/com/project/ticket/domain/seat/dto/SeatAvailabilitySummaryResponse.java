package com.project.ticket.domain.seat.dto;

import java.util.List;

public record SeatAvailabilitySummaryResponse(
        long totalAvailable,
        List<SeatSectionSummaryResponse> sections
) {
}
