package com.project.ticket.domain.payment.entity;

import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Payment() {
    }

    public static Payment succeeded(User user, Seat seat, int amount) {
        return create(user, seat, amount, PaymentStatus.SUCCEEDED);
    }

    public static Payment failed(User user, Seat seat, int amount) {
        return create(user, seat, amount, PaymentStatus.FAILED);
    }

    private static Payment create(User user, Seat seat, int amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.user = user;
        payment.seat = seat;
        payment.amount = amount;
        payment.status = status;
        payment.createdAt = LocalDateTime.now();
        return payment;
    }

}
