package com.project.ticket.support;

import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import com.project.ticket.infra.lock.SeatLockManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class InMemorySeatLockManager implements SeatLockManager {
    private final Map<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public <T> T executeWithLock(Long seatId, Supplier<T> supplier) {
        ReentrantLock lock = locks.computeIfAbsent(seatId, ignored -> new ReentrantLock());
        if (!lock.tryLock()) {
            throw new BusinessException(ErrorCode.SEAT_LOCK_FAILED);
        }
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }
}
