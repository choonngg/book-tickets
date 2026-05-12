package com.project.ticket.domain.ticket.service.purchase;

import com.project.ticket.domain.seat.entity.Seat;
import java.util.Optional;

@FunctionalInterface
public interface SeatReader {
    Optional<Seat> find(Long seatId);
}
