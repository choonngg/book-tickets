package com.project.ticket.domain.seat.dto;

import com.project.ticket.domain.seat.entity.Seat;

public record SeatSectionSeatResponse(
        Long seatId,
        int row,
        int col,
        int price
) {
    public static SeatSectionSeatResponse from(Seat seat) {
        return new SeatSectionSeatResponse(
                seat.getId(),
                seat.getRow(),
                seat.getCol(),
                seat.getPrice()
        );
    }
}
