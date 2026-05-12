package com.project.ticket.domain.concert.service;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import com.project.ticket.domain.concert.dto.ConcertResponse;
import com.project.ticket.domain.concert.entity.Concert;
import com.project.ticket.domain.concert.repository.ConcertRepository;
import com.project.ticket.domain.seat.entity.Seat;
import com.project.ticket.domain.seat.repository.SeatRepository;
import com.project.ticket.domain.user.entity.User;
import com.project.ticket.domain.user.entity.UserRole;
import com.project.ticket.domain.user.repository.UserRepository;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConcertService {
    private static final List<String> DEFAULT_SECTIONS = List.of(
            "A", "B",
            "C", "D", "E", "F",
            "G", "H", "I", "J"
    );
    private static final int DEFAULT_SECTION_ROW_COUNT = 50;
    private static final int DEFAULT_SECTION_COL_COUNT = 30;

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public ConcertService(
            ConcertRepository concertRepository,
            SeatRepository seatRepository,
            UserRepository userRepository
    ) {
        this.concertRepository = concertRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ConcertResponse createConcert(Long artistId, ConcertCreateRequest request) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (artist.getRole() != UserRole.ARTIST) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Concert concert = concertRepository.save(Concert.create(
                artist,
                request.title(),
                request.venue(),
                request.concertDate(),
                request.ticketOpenDate(),
                request.ticketCloseDate()
        ));
        seatRepository.saveAll(createSeats(concert, request));
        return ConcertResponse.from(concert);
    }

    @Transactional(readOnly = true)
    public List<ConcertResponse> findConcerts() {
        return concertRepository.findAll().stream()
                .map(ConcertResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConcertResponse findConcert(Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONCERT_NOT_FOUND));
        return ConcertResponse.from(concert);
    }

    private List<Seat> createSeats(Concert concert, ConcertCreateRequest request) {
        List<Seat> seats = new ArrayList<>();
        for (String section : DEFAULT_SECTIONS) {
            for (int row = 1; row <= DEFAULT_SECTION_ROW_COUNT; row++) {
                for (int col = 1; col <= DEFAULT_SECTION_COL_COUNT; col++) {
                    seats.add(Seat.create(concert, section, row, col, request.price()));
                }
            }
        }
        return seats;
    }
}
