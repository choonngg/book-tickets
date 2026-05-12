package com.project.ticket.infra.payment;

public record PaymentResult(boolean successful, Long paymentId) {
    public static PaymentResult success(Long paymentId) {
        return new PaymentResult(true, paymentId);
    }

    public static PaymentResult failure(Long paymentId) {
        return new PaymentResult(false, paymentId);
    }
}
