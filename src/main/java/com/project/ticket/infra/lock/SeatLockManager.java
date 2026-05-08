package com.project.ticket.infra.lock;

import java.util.function.Supplier;

public interface SeatLockManager {
    <T> T executeWithLock(Long seatId, Supplier<T> supplier);
}
