package com.project.ticket.web.form;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record ConcertCreateForm(
        String title,
        String venue,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime concertDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ticketOpenDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ticketCloseDate,
        int rowCount,
        int colCount,
        int price
) {
    public ConcertCreateRequest toRequest() {
        return new ConcertCreateRequest(
                title,
                venue,
                concertDate,
                ticketOpenDate,
                ticketCloseDate,
                rowCount,
                colCount,
                price
        );
    }
}
