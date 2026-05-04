package com.project.ticket.domain.seat.dto;

import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.seat.entity.SeatStatus;

public record SeatResponse(
        Long seatId,
        Long concertId,
        String section,
        int row,
        int col,
        int price,
        SeatStatus status
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getConcert().getId(),
                seat.getSection(),
                seat.getRow(),
                seat.getCol(),
                seat.getPrice(),
                seat.getStatus()
        );
    }
}
