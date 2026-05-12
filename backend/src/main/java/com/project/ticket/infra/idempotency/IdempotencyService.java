package com.project.ticket.infra.idempotency;

import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import java.time.Duration;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {
    private static final Duration DEFAULT_TTL = Duration.ofHours(24);

    private final IdempotencyStore idempotencyStore;

    public IdempotencyService(IdempotencyStore idempotencyStore) {
        this.idempotencyStore = idempotencyStore;
    }

    public Optional<TicketPurchaseResponse> findTicketPurchaseResponse(Long userId, String idempotencyKey) {
        return idempotencyStore.find(ticketPurchaseKey(userId, idempotencyKey));
    }

    public void saveTicketPurchaseResponse(Long userId, String idempotencyKey, TicketPurchaseResponse response) {
        saveTicketPurchaseResponse(userId, idempotencyKey, response, DEFAULT_TTL);
    }

    public void saveTicketPurchaseResponse(
            Long userId,
            String idempotencyKey,
            TicketPurchaseResponse response,
            Duration ttl
    ) {
        idempotencyStore.save(ticketPurchaseKey(userId, idempotencyKey), response, ttl);
    }

    private String ticketPurchaseKey(Long userId, String idempotencyKey) {
        return "idempotency:tickets:" + userId + ":" + idempotencyKey;
    }
}
