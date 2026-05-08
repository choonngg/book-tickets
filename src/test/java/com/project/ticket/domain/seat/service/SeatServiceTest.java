package com.project.ticket.domain.seat.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import com.project.ticket.domain.concert.service.ConcertService;
import com.project.ticket.domain.seat.entity.SeatStatus;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SeatServiceTest {

    @Autowired
    SeatService seatService;

    @Autowired
    ConcertService concertService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SeatRepository seatRepository;

    @Test
    void findsAllSeatsForConcert() {
        step("조회 대상 콘서트와 좌석을 생성한다.");
        Long concertId = createConcert();

        step("콘서트의 전체 좌석을 조회한다.");
        var seats = seatService.findSeats(concertId);

        step("전체 좌석 수와 대표 좌석 정보를 검증한다.");
        assertThat(seats).hasSize(4);
        assertThat(seats)
                .extracting("section", "row", "col", "price", "status")
                .contains(
                        org.assertj.core.groups.Tuple.tuple("A", 1, 1, 50_000, SeatStatus.AVAILABLE),
                        org.assertj.core.groups.Tuple.tuple("A", 2, 2, 50_000, SeatStatus.AVAILABLE)
                );
    }

    @Test
    void findsOnlyAvailableSeatsForConcert() {
        step("조회 대상 콘서트와 좌석을 생성한다.");
        Long concertId = createConcert();
        step("좌석 하나를 HELD 상태로 변경한다.");
        var heldSeat = seatRepository.findByConcertId(concertId).getFirst();
        heldSeat.hold();

        step("예매 가능한 좌석만 조회한다.");
        var seats = seatService.findAvailableSeats(concertId);

        step("HELD 좌석이 제외되고 AVAILABLE 좌석만 남았는지 검증한다.");
        assertThat(seats).hasSize(3);
        assertThat(seats).allMatch(seat -> seat.status() == SeatStatus.AVAILABLE);
    }

    private Long createConcert() {
        User artist = userRepository.save(User.createArtist("Artist"));
        var request = new ConcertCreateRequest(
                "Spring Concert",
                "Olympic Park",
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(29),
                2,
                2,
                50_000
        );
        return concertService.createConcert(artist.getId(), request).concertId();
    }

    private void step(String message) {
        System.out.println("[TEST STEP] " + message);
    }
}
