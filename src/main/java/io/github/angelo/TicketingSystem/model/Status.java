package io.github.angelo.TicketingSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "oldStatus", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StatusHistory> oldStatusHistories = new ArrayList<>();

    @OneToMany(mappedBy = "newStatus", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StatusHistory> newStatusHistories = new ArrayList<>();
}
