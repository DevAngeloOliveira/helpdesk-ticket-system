package io.github.angelo.TicketingSystem.controller;

import io.github.angelo.TicketingSystem.dto.request.StatusRequest;
import io.github.angelo.TicketingSystem.dto.response.StatusResponse;
import io.github.angelo.TicketingSystem.service.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @PostMapping
    public ResponseEntity<StatusResponse> createStatus(@Valid @RequestBody StatusRequest request) {
        StatusResponse response = statusService.createStatus(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusResponse> getStatusById(@PathVariable Long id) {
        StatusResponse response = statusService.getStatusById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StatusResponse>> getAllStatuses() {
        List<StatusResponse> responses = statusService.getAllStatuses();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponse> updateStatus(
            @PathVariable Long id, 
            @Valid @RequestBody StatusRequest request) {
        StatusResponse response = statusService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
