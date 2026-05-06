package com.project.ticket.domain.ticket.service;

import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.ticket.dto.TicketPurchaseRequest;
import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import com.project.ticket.domain.ticket.entity.Ticket;
import com.project.ticket.domain.ticket.repository.TicketRepository;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.repository.UserRepository;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import com.project.ticket.infra.idempotency.IdempotencyService;
import com.project.ticket.infra.payment.PaymentClient;
import com.project.ticket.infra.payment.PaymentResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;
    private final IdempotencyService idempotencyService;

    public TicketService(
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            UserRepository userRepository,
            PaymentClient paymentClient,
            IdempotencyService idempotencyService
    ) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public TicketPurchaseResponse purchase(
            Long userId,
            TicketPurchaseRequest request,
            String idempotencyKey
    ) {
        if (!StringUtils.hasText(idempotencyKey)) {
            throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        return idempotencyService.findTicketPurchaseResponse(userId, idempotencyKey)
                .orElseGet(() -> purchaseNew(userId, request, idempotencyKey));
    }

    private TicketPurchaseResponse purchaseNew(Long userId, TicketPurchaseRequest request, String idempotencyKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Seat seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_NOT_FOUND));

        seat.hold();
        PaymentResult paymentResult = paymentClient.pay(userId, seat.getId(), seat.getPrice());
        if (!paymentResult.successful()) {
            seat.release();
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        seat.completeSale();
        Ticket ticket = ticketRepository.save(Ticket.complete(user, seat, paymentResult.paymentId()));
        TicketPurchaseResponse response = TicketPurchaseResponse.from(ticket);
        idempotencyService.saveTicketPurchaseResponse(userId, idempotencyKey, response);
        return response;
    }
}
