package com.project.ticket.domain.ticket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TicketPurchaseRequest(@NotNull @Positive Long seatId) {
}
