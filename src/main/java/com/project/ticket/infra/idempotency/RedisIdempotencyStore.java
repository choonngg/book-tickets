package com.project.ticket.infra.idempotency;

import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import java.time.Duration;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
@Profile("!test")
public class RedisIdempotencyStore implements IdempotencyStore {
    private final StringRedisTemplate redisTemplate;
    private final JsonMapper jsonMapper;

    public RedisIdempotencyStore(StringRedisTemplate redisTemplate, JsonMapper jsonMapper) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Optional<TicketPurchaseResponse> find(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(jsonMapper.readValue(value, TicketPurchaseResponse.class));
        } catch (JacksonException exception) {
            throw new IllegalStateException("Failed to read idempotency response.", exception);
        }
    }

    @Override
    public void save(String key, TicketPurchaseResponse response, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, jsonMapper.writeValueAsString(response), ttl);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Failed to write idempotency response.", exception);
        }
    }
}
