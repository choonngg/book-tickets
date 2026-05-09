package com.project.ticket.domain.concert.controller;

import com.project.ticket.domain.concert.dto.ConcertCreateRequest;
import com.project.ticket.domain.concert.dto.ConcertResponse;
import com.project.ticket.domain.concert.service.ConcertService;
import com.project.ticket.global.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {
    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @GetMapping
    public List<ConcertResponse> findConcerts() {
        return concertService.findConcerts();
    }

    @GetMapping("/{concertId}")
    public ConcertResponse findConcert(@PathVariable Long concertId) {
        return concertService.findConcert(concertId);
    }

    @PostMapping
    public ResponseEntity<ConcertResponse> createConcert(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ConcertCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(concertService.createConcert(user.userId(), request));
    }
}
