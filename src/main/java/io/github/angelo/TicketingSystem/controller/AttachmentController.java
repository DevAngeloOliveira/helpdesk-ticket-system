package io.github.angelo.TicketingSystem.controller;

import io.github.angelo.TicketingSystem.dto.request.AttachmentRequest;
import io.github.angelo.TicketingSystem.dto.response.AttachmentResponse;
import io.github.angelo.TicketingSystem.service.AttachmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<AttachmentResponse> createAttachment(@Valid @RequestBody AttachmentRequest request) {
        AttachmentResponse response = attachmentService.createAttachment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentResponse> getAttachmentById(@PathVariable Long id) {
        AttachmentResponse response = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<AttachmentResponse>> getAttachmentsByTicket(@PathVariable Long ticketId) {
        List<AttachmentResponse> responses = attachmentService.getAttachmentsByTicket(ticketId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
