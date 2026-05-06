package com.project.ticket.infra.idempotency;

import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import java.time.Duration;
import java.util.Optional;

public interface IdempotencyStore {
    Optional<TicketPurchaseResponse> find(String key);

    void save(String key, TicketPurchaseResponse response, Duration ttl);
}
