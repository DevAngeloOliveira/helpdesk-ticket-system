package io.github.angelo.TicketingSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {

    @NotBlank(message = "Status name is required")
    @Size(min = 2, max = 50, message = "Status name must be between 2 and 50 characters")
    private String name;
}
