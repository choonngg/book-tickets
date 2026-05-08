package com.project.ticket.domain.concert.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
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
class ConcertServiceTest {

    @Autowired
    ConcertService concertService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SeatRepository seatRepository;

    @Test
    void artistCreatesConcertWithSeats() {
        step("아티스트 사용자를 생성한다.");
        User artist = userRepository.save(User.createArtist("Artist"));
        step("2x3 좌석을 가진 콘서트 생성 요청을 준비한다.");
        var request = new ConcertCreateRequest(
                "Spring Concert",
                "Olympic Park",
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(29),
                2,
                3,
                100_000
        );

        step("콘서트를 생성한다.");
        var response = concertService.createConcert(artist.getId(), request);

        step("콘서트 제목과 자동 생성된 좌석 수를 검증한다.");
        assertThat(response.title()).isEqualTo("Spring Concert");
        assertThat(seatRepository.findByConcertId(response.concertId())).hasSize(6);
    }

    private void step(String message) {
        System.out.println("[TEST STEP] " + message);
    }
}
