package com.project.ticket.web.form;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record ConcertCreateForm(
        @NotBlank(message = "공연명을 입력해주세요.")
        String title,
        @NotBlank(message = "장소를 입력해주세요.")
        String venue,
        @NotNull(message = "공연일을 입력해주세요.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime concertDate,
        @NotNull(message = "예매 시작일을 입력해주세요.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ticketOpenDate,
        @NotNull(message = "예매 종료일을 입력해주세요.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ticketCloseDate,
        @Positive(message = "행 수는 1 이상이어야 합니다.")
        int rowCount,
        @Positive(message = "열 수는 1 이상이어야 합니다.")
        int colCount,
        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
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
