package com.project.ticket.domain.seat.controller;

import com.project.ticket.domain.seat.dto.SeatAvailabilitySummaryResponse;
import com.project.ticket.domain.seat.dto.SeatResponse;
import com.project.ticket.domain.seat.dto.SeatSectionAvailabilityResponse;
import com.project.ticket.domain.seat.service.SeatService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concerts/{concertId}/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public List<SeatResponse> findSeats(@PathVariable Long concertId) {
        return seatService.findSeats(concertId);
    }

    @GetMapping("/available")
    public List<SeatResponse> findAvailableSeats(@PathVariable Long concertId) {
        return seatService.findAvailableSeats(concertId);
    }

    @GetMapping("/availability")
    public SeatAvailabilitySummaryResponse findSeatAvailabilitySummary(@PathVariable Long concertId) {
        return seatService.findAvailabilitySummary(concertId);
    }

    @GetMapping("/availability/sections/{section}")
    public SeatSectionAvailabilityResponse findSeatSectionAvailability(
            @PathVariable Long concertId,
            @PathVariable String section
    ) {
        return seatService.findSectionAvailability(concertId, section);
    }
}
