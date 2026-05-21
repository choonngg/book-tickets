package com.project.ticket.domain.seat.repository;

import com.project.ticket.domain.seat.dto.SeatSectionSummaryResponse;
import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.seat.entity.SeatStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByConcertId(Long concertId);

    List<Seat> findByConcertIdAndStatus(Long concertId, SeatStatus status);

    @Query("""
            select new com.project.ticket.domain.seat.dto.SeatSectionSummaryResponse(s.section, count(s))
            from Seat s
            where s.concert.id = :concertId and s.status = :status
            group by s.section
            order by s.section
            """)
    List<SeatSectionSummaryResponse> countAvailableSeatsBySection(
            @Param("concertId") Long concertId,
            @Param("status") SeatStatus status
    );

    long countByConcertIdAndSectionAndStatus(Long concertId, String section, SeatStatus status);

    List<Seat> findByConcertIdAndSectionAndStatusOrderByRowAscColAsc(
            Long concertId,
            String section,
            SeatStatus status
    );

    List<Seat> findByConcertIdAndSectionOrderByRowAscColAsc(Long concertId, String section);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = :seatId")
    Optional<Seat> findByIdForUpdate(@Param("seatId") Long seatId);
}
