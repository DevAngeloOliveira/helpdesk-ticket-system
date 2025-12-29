package io.github.angelo.TicketingSystem.service;

import io.github.angelo.TicketingSystem.dto.request.AttachmentRequest;
import io.github.angelo.TicketingSystem.dto.response.AttachmentResponse;
import io.github.angelo.TicketingSystem.exception.ResourceNotFoundException;
import io.github.angelo.TicketingSystem.model.Attachment;
import io.github.angelo.TicketingSystem.model.Ticket;
import io.github.angelo.TicketingSystem.repository.AttachmentRepository;
import io.github.angelo.TicketingSystem.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public AttachmentResponse createAttachment(AttachmentRequest request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        Attachment attachment = Attachment.builder()
                .fileName(request.getFileName())
                .fileUrl(request.getFileUrl())
                .ticket(ticket)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return mapToResponse(savedAttachment);
    }

    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));
        return mapToResponse(attachment);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByTicket(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }
        return attachmentRepository.findByTicketId(ticketId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAttachment(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attachment not found with id: " + id);
        }
        attachmentRepository.deleteById(id);
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .uploadedAt(attachment.getUploadedAt())
                .ticketId(attachment.getTicket().getId())
                .build();
    }
}
