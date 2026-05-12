package com.project.ticket.domain.concert.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ConcertServiceTest {

    @Autowired
    ConcertService concertService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SeatRepository seatRepository;

    @Test
    void artistCreatesConcertWithDefaultSeatLayout() {
        User artist = userRepository.save(User.createArtist("Artist"));
        var request = new ConcertCreateRequest(
                "Spring Concert",
                "Olympic Park",
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(29),
                100_000
        );

        var response = concertService.createConcert(artist.getId(), request);
        List<Seat> seats = seatRepository.findByConcertId(response.concertId());

        assertThat(response.title()).isEqualTo("Spring Concert");
        assertThat(seats).hasSize(15_000);
        assertThat(seats)
                .extracting(Seat::getSection)
                .containsOnly("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        assertThat(seats)
                .filteredOn(seat -> seat.getSection().equals("A"))
                .hasSize(1_500)
                .extracting(Seat::getRow, Seat::getCol)
                .contains(
                        org.assertj.core.groups.Tuple.tuple(1, 1),
                        org.assertj.core.groups.Tuple.tuple(50, 30)
                );
    }
}
