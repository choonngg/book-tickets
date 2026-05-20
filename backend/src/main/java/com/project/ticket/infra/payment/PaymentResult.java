package com.project.ticket.infra.payment;

import com.project.ticket.domain.payment.entity.Payment;

public record PaymentResult(boolean successful, Payment payment) {
    public static PaymentResult success(Payment payment) {
        return new PaymentResult(true, payment);
    }

    public static PaymentResult failure(Payment payment) {
        return new PaymentResult(false, payment);
    }

    public static PaymentResult successWithoutPayment() {
        return new PaymentResult(true, null);
    }
}
