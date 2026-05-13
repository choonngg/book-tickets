package com.project.ticket.domain.seat.entity;

import com.project.ticket.domain.concert.entity.Concert;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_seat_concert_location",
                        columnNames = {"concert_id", "section", "seat_row", "seat_col"}
                )
        }
)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(nullable = false, length = 20)
    private String section;

    @Column(name = "seat_row", nullable = false)
    private int row;

    @Column(name = "seat_col", nullable = false)
    private int col;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Version
    private Long version;

    protected Seat() {
    }

    public static Seat create(Concert concert, String section, int row, int col, int price) {
        Seat seat = new Seat();
        seat.concert = concert;
        seat.section = section;
        seat.row = row;
        seat.col = col;
        seat.price = price;
        seat.status = SeatStatus.AVAILABLE;
        return seat;
    }

    public void hold() {
        if (status != SeatStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.SEAT_NOT_AVAILABLE);
        }
        status = SeatStatus.HELD;
    }

    public void completeSale() {
        if (status != SeatStatus.HELD) {
            throw new BusinessException(ErrorCode.SEAT_NOT_AVAILABLE);
        }
        status = SeatStatus.SOLD;
    }

    public void release() {
        status = SeatStatus.AVAILABLE;
    }

}
