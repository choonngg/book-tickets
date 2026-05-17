package com.project.ticket.domain.seat.dto;

import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.seat.entity.SeatStatus;

public record SeatSectionSeatResponse(
        Long seatId,
        int row,
        int col,
        int price,
        SeatStatus status
) {
    public static SeatSectionSeatResponse from(Seat seat) {
        return new SeatSectionSeatResponse(
                seat.getId(),
                seat.getRow(),
                seat.getCol(),
                seat.getPrice(),
                seat.getStatus()
        );
    }
}
