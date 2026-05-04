package com.project.ticket.domain.seat.service;

import com.project.ticket.domain.concert.repository.ConcertRepository;
import com.project.ticket.domain.seat.dto.SeatResponse;
import com.project.ticket.domain.seat.entity.SeatStatus;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;

    public SeatService(SeatRepository seatRepository, ConcertRepository concertRepository) {
        this.seatRepository = seatRepository;
        this.concertRepository = concertRepository;
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> findSeats(Long concertId) {
        verifyConcertExists(concertId);
        return seatRepository.findByConcertId(concertId).stream()
                .map(SeatResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> findAvailableSeats(Long concertId) {
        verifyConcertExists(concertId);
        return seatRepository.findByConcertIdAndStatus(concertId, SeatStatus.AVAILABLE).stream()
                .map(SeatResponse::from)
                .toList();
    }

    private void verifyConcertExists(Long concertId) {
        if (!concertRepository.existsById(concertId)) {
            throw new BusinessException(ErrorCode.CONCERT_NOT_FOUND);
        }
    }
}
