package io.github.angelo.TicketingSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityResponse {
    private Long id;
    private String name;
    private Integer level;
}
