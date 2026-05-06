package com.project.ticket.infra.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import java.time.Duration;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class RedisIdempotencyStore implements IdempotencyStore {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisIdempotencyStore(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<TicketPurchaseResponse> find(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(value, TicketPurchaseResponse.class));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to read idempotency response.", exception);
        }
    }

    @Override
    public void save(String key, TicketPurchaseResponse response, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(response), ttl);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to write idempotency response.", exception);
        }
    }
}
