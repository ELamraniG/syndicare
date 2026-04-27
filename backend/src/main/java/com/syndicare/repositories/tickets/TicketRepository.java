package com.syndicare.repositories.tickets;

import com.syndicare.domain.entities.tickets.Ticket;
import com.syndicare.domain.entities.tickets.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findBySubmitterIdOrderByCreatedAtDesc(Long submitterId);
    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);
    List<Ticket> findAllByOrderByCreatedAtDesc();
    long countByStatus(TicketStatus status);
}
