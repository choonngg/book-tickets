package com.project.ticket.domain.ticket.service.purchase;

import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;

@FunctionalInterface
public interface TicketPurchaseStrategy {
    TicketPurchaseResponse purchase(Long userId, Long seatId);
}
