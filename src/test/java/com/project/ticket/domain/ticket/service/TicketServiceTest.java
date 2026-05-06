package com.project.ticket.domain.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import com.project.ticket.domain.concert.service.ConcertService;
import com.project.ticket.domain.seat.entity.SeatStatus;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.ticket.dto.TicketPurchaseRequest;
import com.project.ticket.domain.ticket.entity.TicketStatus;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.infra.payment.MockPaymentClient;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TicketServiceTest {

    @Autowired
    TicketService ticketService;

    @Autowired
    ConcertService concertService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    MockPaymentClient mockPaymentClient;

    @Test
    void purchaseCreatesTicketAndSellsSeat() {
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        var response = ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "key-1");

        assertThat(response.status()).isEqualTo(TicketStatus.COMPLETED);
        assertThat(response.seatId()).isEqualTo(seatId);
        assertThat(seatRepository.findById(seatId).orElseThrow().getStatus()).isEqualTo(SeatStatus.SOLD);
    }

    @Test
    void paymentFailureReleasesSeat() {
        mockPaymentClient.failNextPayment();
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        assertThatThrownBy(() -> ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "key-2"))
                .isInstanceOf(BusinessException.class);

        assertThat(seatRepository.findById(seatId).orElseThrow().getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    void missingIdempotencyKeyFails() {
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        assertThatThrownBy(() -> ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), ""))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void sameIdempotencyKeyReturnsPreviousResponse() {
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        var first = ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "same-key");
        var second = ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "same-key");

        assertThat(second).isEqualTo(first);
    }

    private Long createFan() {
        return userRepository.save(User.createFan("Fan")).getId();
    }

    private Long createConcertSeat() {
        User artist = userRepository.save(User.createArtist("Artist"));
        var request = new ConcertCreateRequest(
                "Spring Concert",
                "Olympic Park",
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(29),
                1,
                1,
                100_000
        );
        Long concertId = concertService.createConcert(artist.getId(), request).concertId();
        return seatRepository.findByConcertId(concertId).getFirst().getId();
    }
}
