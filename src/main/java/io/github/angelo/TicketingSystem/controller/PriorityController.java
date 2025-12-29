package io.github.angelo.TicketingSystem.controller;

import io.github.angelo.TicketingSystem.dto.request.PriorityRequest;
import io.github.angelo.TicketingSystem.dto.response.PriorityResponse;
import io.github.angelo.TicketingSystem.service.PriorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
@RequiredArgsConstructor
public class PriorityController {

    private final PriorityService priorityService;

    @PostMapping
    public ResponseEntity<PriorityResponse> createPriority(@Valid @RequestBody PriorityRequest request) {
        PriorityResponse response = priorityService.createPriority(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriorityResponse> getPriorityById(@PathVariable Long id) {
        PriorityResponse response = priorityService.getPriorityById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PriorityResponse>> getAllPriorities() {
        List<PriorityResponse> responses = priorityService.getAllPriorities();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriorityResponse> updatePriority(
            @PathVariable Long id, 
            @Valid @RequestBody PriorityRequest request) {
        PriorityResponse response = priorityService.updatePriority(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriority(@PathVariable Long id) {
        priorityService.deletePriority(id);
        return ResponseEntity.noContent().build();
    }
}
