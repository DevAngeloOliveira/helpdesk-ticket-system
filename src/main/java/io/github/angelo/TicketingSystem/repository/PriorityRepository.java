package io.github.angelo.TicketingSystem.repository;

import io.github.angelo.TicketingSystem.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    Optional<Priority> findByName(String name);
    boolean existsByName(String name);
}
