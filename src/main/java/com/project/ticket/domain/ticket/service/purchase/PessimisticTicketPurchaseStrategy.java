package com.project.ticket.domain.ticket.service.purchase;

import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class PessimisticTicketPurchaseStrategy implements TicketPurchaseStrategy {
    private final TicketPurchaseProcessor processor;
    private final SeatRepository seatRepository;

    public PessimisticTicketPurchaseStrategy(TicketPurchaseProcessor processor, SeatRepository seatRepository) {
        this.processor = processor;
        this.seatRepository = seatRepository;
    }

    @Override
    public TicketPurchaseResponse purchase(Long userId, Long seatId) {
        try {
            return processor.purchase(userId, seatId, seatRepository::findByIdForUpdate);
        } catch (PessimisticLockingFailureException | DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.SEAT_LOCK_FAILED);
        }
    }
}
