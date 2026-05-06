package com.project.ticket.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Payment() {
    }

    public static Payment succeeded(Long userId, Long seatId, int amount) {
        return create(userId, seatId, amount, PaymentStatus.SUCCEEDED);
    }

    public static Payment failed(Long userId, Long seatId, int amount) {
        return create(userId, seatId, amount, PaymentStatus.FAILED);
    }

    private static Payment create(Long userId, Long seatId, int amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.userId = userId;
        payment.seatId = seatId;
        payment.amount = amount;
        payment.status = status;
        payment.createdAt = LocalDateTime.now();
        return payment;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public int getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
