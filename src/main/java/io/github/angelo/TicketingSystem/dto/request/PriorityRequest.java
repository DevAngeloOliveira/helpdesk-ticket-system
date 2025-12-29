package io.github.angelo.TicketingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityRequest {

    @NotBlank(message = "Priority name is required")
    @Size(min = 2, max = 50, message = "Priority name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Level is required")
    @Positive(message = "Level must be a positive number")
    private Integer level;
}
