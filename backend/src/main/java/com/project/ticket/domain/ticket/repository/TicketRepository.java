package com.project.ticket.domain.ticket.repository;

import com.project.ticket.domain.ticket.entity.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countBySeatId(Long seatId);

    List<Ticket> findByUserIdOrderByCreatedAtDesc(Long userId);
}
