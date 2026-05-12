package com.project.ticket.support;

import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import com.project.ticket.infra.idempotency.IdempotencyStore;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class InMemoryIdempotencyStore implements IdempotencyStore {
    private final Map<String, TicketPurchaseResponse> responses = new ConcurrentHashMap<>();

    @Override
    public Optional<TicketPurchaseResponse> find(String key) {
        return Optional.ofNullable(responses.get(key));
    }

    @Override
    public void save(String key, TicketPurchaseResponse response, Duration ttl) {
        responses.put(key, response);
    }
}
