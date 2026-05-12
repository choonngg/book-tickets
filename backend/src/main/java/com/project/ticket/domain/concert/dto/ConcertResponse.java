package com.project.ticket.domain.concert.dto;

import com.project.ticket.domain.concert.entity.Concert;
import com.project.ticket.domain.concert.entity.ConcertStatus;
import java.time.LocalDateTime;

public record ConcertResponse(
        Long concertId,
        Long artistId,
        String title,
        String venue,
        LocalDateTime concertDate,
        LocalDateTime ticketOpenDate,
        LocalDateTime ticketCloseDate,
        ConcertStatus status
) {
    public static ConcertResponse from(Concert concert) {
        return new ConcertResponse(
                concert.getId(),
                concert.getArtist().getId(),
                concert.getTitle(),
                concert.getVenue(),
                concert.getConcertDate(),
                concert.getTicketOpenDate(),
                concert.getTicketCloseDate(),
                concert.getStatus()
        );
    }
}
