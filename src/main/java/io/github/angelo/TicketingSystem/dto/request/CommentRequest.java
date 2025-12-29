package io.github.angelo.TicketingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Message is required")
    @Size(min = 1, message = "Message must not be empty")
    private String message;

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
