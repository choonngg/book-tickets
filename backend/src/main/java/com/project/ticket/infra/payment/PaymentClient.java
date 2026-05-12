package com.project.ticket.infra.payment;

public interface PaymentClient {
    PaymentResult pay(Long userId, Long seatId, int amount);

    PaymentResult cancel(Long paymentId);
}
