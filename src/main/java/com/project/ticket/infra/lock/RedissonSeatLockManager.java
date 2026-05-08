package com.project.ticket.infra.lock;

import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class RedissonSeatLockManager implements SeatLockManager {
    private final RedissonClient redissonClient;

    public RedissonSeatLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> T executeWithLock(Long seatId, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock("lock:seat:" + seatId);
        boolean locked;
        try {
            locked = lock.tryLock(0, 10, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SEAT_LOCK_FAILED);
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.SEAT_LOCK_FAILED);
        }
        try {
            return supplier.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
