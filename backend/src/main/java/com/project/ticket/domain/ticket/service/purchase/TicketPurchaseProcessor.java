package com.project.ticket.domain.ticket.service.purchase;

import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import com.project.ticket.domain.ticket.entity.Ticket;
import com.project.ticket.domain.ticket.repository.TicketRepository;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import com.project.ticket.infra.payment.PaymentClient;
import com.project.ticket.infra.payment.PaymentResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TicketPurchaseProcessor {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;

    public TicketPurchaseProcessor(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            PaymentClient paymentClient
    ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public TicketPurchaseResponse purchase(Long userId, Long seatId, SeatReader seatReader) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Seat seat = seatReader.find(seatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_NOT_FOUND));

        seat.hold();
        PaymentResult paymentResult = paymentClient.pay(user, seat, seat.getPrice());
        if (!paymentResult.successful()) {
            seat.release();
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        seat.completeSale();
        Ticket ticket = ticketRepository.save(Ticket.complete(user, seat, paymentResult.payment()));
        return TicketPurchaseResponse.from(ticket);
    }
}
