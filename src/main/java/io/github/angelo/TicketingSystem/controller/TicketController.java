package io.github.angelo.TicketingSystem.controller;

import io.github.angelo.TicketingSystem.dto.request.StatusUpdateRequest;
import io.github.angelo.TicketingSystem.dto.request.TicketRequest;
import io.github.angelo.TicketingSystem.dto.response.StatusHistoryResponse;
import io.github.angelo.TicketingSystem.dto.response.TicketResponse;
import io.github.angelo.TicketingSystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        TicketResponse response = ticketService.getTicketById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> responses = ticketService.getAllTickets();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByUser(@PathVariable Long userId) {
        List<TicketResponse> responses = ticketService.getTicketsByUser(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/assigned/{assignedToId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByAssignedUser(@PathVariable Long assignedToId) {
        List<TicketResponse> responses = ticketService.getTicketsByAssignedUser(assignedToId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable Long statusId) {
        List<TicketResponse> responses = ticketService.getTicketsByStatus(statusId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id, 
            @Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @Valid @RequestBody StatusUpdateRequest request) {
        TicketResponse response = ticketService.updateTicketStatus(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusHistoryResponse>> getTicketHistory(@PathVariable Long id) {
        List<StatusHistoryResponse> responses = ticketService.getTicketHistory(id);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
