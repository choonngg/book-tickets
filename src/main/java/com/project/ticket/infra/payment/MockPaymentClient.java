package com.project.ticket.infra.payment;

import com.project.ticket.domain.payment.entity.Payment;
import com.project.ticket.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentClient implements PaymentClient {
    private final PaymentRepository paymentRepository;
    private boolean failNextPayment;

    public MockPaymentClient(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResult pay(Long userId, Long seatId, int amount) {
        if (failNextPayment) {
            failNextPayment = false;
            Payment payment = paymentRepository.save(Payment.failed(userId, seatId, amount));
            return PaymentResult.failure(payment.getId());
        }
        Payment payment = paymentRepository.save(Payment.succeeded(userId, seatId, amount));
        return PaymentResult.success(payment.getId());
    }

    @Override
    public PaymentResult cancel(Long paymentId) {
        return PaymentResult.success(paymentId);
    }

    public void failNextPayment() {
        failNextPayment = true;
    }
}
