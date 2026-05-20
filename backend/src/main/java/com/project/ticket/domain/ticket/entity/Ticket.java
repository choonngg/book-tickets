package com.project.ticket.domain.ticket.entity;

import com.project.ticket.domain.payment.entity.Payment;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "ticket",
        uniqueConstraints = @UniqueConstraint(name = "uq_ticket_seat", columnNames = "seat_id")
)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Ticket() {
    }

    public static Ticket complete(User user, Seat seat, Payment payment) {
        Ticket ticket = new Ticket();
        ticket.user = user;
        ticket.seat = seat;
        ticket.payment = payment;
        ticket.status = TicketStatus.COMPLETED;
        ticket.createdAt = LocalDateTime.now();
        return ticket;
    }

}
