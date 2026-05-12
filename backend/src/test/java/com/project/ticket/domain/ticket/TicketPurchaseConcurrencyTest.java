package com.project.ticket.domain.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import com.project.ticket.domain.concert.service.ConcertService;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.ticket.dto.TicketPurchaseRequest;
import com.project.ticket.domain.ticket.repository.TicketRepository;
import com.project.ticket.domain.ticket.service.TicketService;
import com.project.ticket.domain.ticket.service.purchase.PurchaseStrategy;
import com.project.ticket.domain.ticket.service.purchase.TicketPurchaseProperties;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class TicketPurchaseConcurrencyTest {
    private static final int REQUEST_COUNT = 50;

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketPurchaseProperties properties;

    @Autowired
    ConcertService concertService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    TicketRepository ticketRepository;

    @ParameterizedTest
    @EnumSource(PurchaseStrategy.class)
    void sameSeatConcurrentPurchaseCreatesOnlyOneTicket(PurchaseStrategy strategy) throws Exception {
        properties.setPurchaseStrategy(strategy);
        Long fanId = createFan();
        Long seatId = createConcertSeat();

        long successCount = executeConcurrentPurchases(fanId, seatId);

        assertThat(successCount).isEqualTo(1);
        assertThat(ticketRepository.countBySeatId(seatId)).isEqualTo(1);
    }

    private long executeConcurrentPurchases(Long fanId, Long seatId) throws Exception {
        CountDownLatch ready = new CountDownLatch(REQUEST_COUNT);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(REQUEST_COUNT);
        try {
            List<Future<Boolean>> results = IntStream.range(0, REQUEST_COUNT)
                    .mapToObj(index -> executor.submit(() -> {
                        ready.countDown();
                        start.await();
                        try {
                            ticketService.purchase(fanId, new TicketPurchaseRequest(seatId), "key-" + index);
                            return true;
                        } catch (Exception exception) {
                            return false;
                        }
                    }))
                    .toList();

            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            long successCount = 0;
            for (Future<Boolean> result : results) {
                if (result.get(10, TimeUnit.SECONDS)) {
                    successCount++;
                }
            }
            return successCount;
        } finally {
            executor.shutdownNow();
        }
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
                100_000
        );
        Long concertId = concertService.createConcert(artist.getId(), request).concertId();
        return seatRepository.findByConcertId(concertId).getFirst().getId();
    }
}
