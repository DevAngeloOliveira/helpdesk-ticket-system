package io.github.angelo.TicketingSystem.repository;

import io.github.angelo.TicketingSystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByAssignedToId(Long assignedToId);
    List<Ticket> findByCategoryId(Long categoryId);
    List<Ticket> findByStatusId(Long statusId);
    List<Ticket> findByPriorityId(Long priorityId);
}
