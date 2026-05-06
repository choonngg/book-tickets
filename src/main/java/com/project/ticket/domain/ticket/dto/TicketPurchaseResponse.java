package com.project.ticket.domain.ticket.dto;

import com.project.ticket.domain.ticket.entity.Ticket;
import com.project.ticket.domain.ticket.entity.TicketStatus;

public record TicketPurchaseResponse(
        Long ticketId,
        Long userId,
        Long seatId,
        Long paymentId,
        TicketStatus status
) {
    public static TicketPurchaseResponse from(Ticket ticket) {
        return new TicketPurchaseResponse(
                ticket.getId(),
                ticket.getUser().getId(),
                ticket.getSeat().getId(),
                ticket.getPaymentId(),
                ticket.getStatus()
        );
    }
}
