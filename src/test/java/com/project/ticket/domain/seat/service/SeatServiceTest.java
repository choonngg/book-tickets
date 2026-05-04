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
        Long concertId = createConcert();

        var seats = seatService.findSeats(concertId);

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
        Long concertId = createConcert();
        var heldSeat = seatRepository.findByConcertId(concertId).getFirst();
        heldSeat.hold();

        var seats = seatService.findAvailableSeats(concertId);

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
}
