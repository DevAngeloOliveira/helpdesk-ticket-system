package io.github.angelo.TicketingSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryResponse {
    private Long id;
    private LocalDateTime changedAt;
    private Long ticketId;
    private StatusResponse oldStatus;
    private StatusResponse newStatus;
    private UserResponse changedBy;
}
