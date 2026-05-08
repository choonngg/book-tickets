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
        step("구매자와 구매할 좌석을 준비한다.");
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        step("idempotency key와 함께 티켓 구매를 요청한다.");
        var response = ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "key-1");

        step("티켓 완료 상태와 좌석 SOLD 상태를 검증한다.");
        assertThat(response.status()).isEqualTo(TicketStatus.COMPLETED);
        assertThat(response.seatId()).isEqualTo(seatId);
        assertThat(seatRepository.findById(seatId).orElseThrow().getStatus()).isEqualTo(SeatStatus.SOLD);
    }

    @Test
    void paymentFailureReleasesSeat() {
        step("다음 결제가 실패하도록 mock payment를 설정한다.");
        mockPaymentClient.failNextPayment();
        step("구매자와 구매할 좌석을 준비한다.");
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        step("티켓 구매가 비즈니스 예외로 실패하는지 검증한다.");
        assertThatThrownBy(() -> ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "key-2"))
                .isInstanceOf(BusinessException.class);

        step("결제 실패 후 좌석이 AVAILABLE로 복구됐는지 검증한다.");
        assertThat(seatRepository.findById(seatId).orElseThrow().getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    void missingIdempotencyKeyFails() {
        step("구매자와 구매할 좌석을 준비한다.");
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        step("빈 idempotency key로 구매하면 비즈니스 예외가 발생하는지 검증한다.");
        assertThatThrownBy(() -> ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), ""))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void sameIdempotencyKeyReturnsPreviousResponse() {
        step("구매자와 구매할 좌석을 준비한다.");
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        step("같은 idempotency key로 구매 요청을 두 번 보낸다.");
        var first = ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "same-key");
        var second = ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "same-key");

        step("두 번째 응답이 첫 번째 응답과 같은지 검증한다.");
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

    private void step(String message) {
        System.out.println("[TEST STEP] " + message);
    }
}
