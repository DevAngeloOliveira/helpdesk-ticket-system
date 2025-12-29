package io.github.angelo.TicketingSystem.service;

import io.github.angelo.TicketingSystem.dto.request.StatusUpdateRequest;
import io.github.angelo.TicketingSystem.dto.request.TicketRequest;
import io.github.angelo.TicketingSystem.dto.response.*;
import io.github.angelo.TicketingSystem.exception.ResourceNotFoundException;
import io.github.angelo.TicketingSystem.model.*;
import io.github.angelo.TicketingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Priority priority = priorityRepository.findById(request.getPriorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found with id: " + request.getPriorityId()));

        // Get default status "Open" or first status
        Status defaultStatus = statusRepository.findByName("Open")
                .orElseGet(() -> statusRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No status found in the system")));

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found with id: " + request.getAssignedToId()));
        }

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .assignedTo(assignedTo)
                .category(category)
                .priority(priority)
                .status(defaultStatus)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        // Create initial status history
        StatusHistory initialHistory = StatusHistory.builder()
                .ticket(savedTicket)
                .oldStatus(null)
                .newStatus(defaultStatus)
                .changedBy(user)
                .build();
        statusHistoryRepository.save(initialHistory);

        return mapToResponse(savedTicket);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByUser(Long userId) {
        return ticketRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByAssignedUser(Long assignedToId) {
        return ticketRepository.findByAssignedToId(assignedToId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByStatus(Long statusId) {
        return ticketRepository.findByStatusId(statusId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse updateTicket(Long id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Priority priority = priorityRepository.findById(request.getPriorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found with id: " + request.getPriorityId()));

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found with id: " + request.getAssignedToId()));
        }

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setAssignedTo(assignedTo);

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }

    @Transactional
    public TicketResponse updateTicketStatus(StatusUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        Status newStatus = statusRepository.findById(request.getNewStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with id: " + request.getNewStatusId()));

        User changedBy = userRepository.findById(request.getChangedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getChangedByUserId()));

        Status oldStatus = ticket.getStatus();

        // Update ticket status
        ticket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Create status history entry
        StatusHistory statusHistory = StatusHistory.builder()
                .ticket(ticket)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .build();
        statusHistoryRepository.save(statusHistory);

        return mapToResponse(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<StatusHistoryResponse> getTicketHistory(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }

        return statusHistoryRepository.findByTicketIdOrderByChangedAtDesc(ticketId).stream()
                .map(this::mapHistoryToResponse)
                .collect(Collectors.toList());
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .user(mapUserToResponse(ticket.getUser()))
                .assignedTo(ticket.getAssignedTo() != null ? mapUserToResponse(ticket.getAssignedTo()) : null)
                .category(mapCategoryToResponse(ticket.getCategory()))
                .priority(mapPriorityToResponse(ticket.getPriority()))
                .status(mapStatusToResponse(ticket.getStatus()))
                .build();
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private CategoryResponse mapCategoryToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    private PriorityResponse mapPriorityToResponse(Priority priority) {
        return PriorityResponse.builder()
                .id(priority.getId())
                .name(priority.getName())
                .level(priority.getLevel())
                .build();
    }

    private StatusResponse mapStatusToResponse(Status status) {
        return StatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .build();
    }

    private StatusHistoryResponse mapHistoryToResponse(StatusHistory history) {
        return StatusHistoryResponse.builder()
                .id(history.getId())
                .changedAt(history.getChangedAt())
                .ticketId(history.getTicket().getId())
                .oldStatus(history.getOldStatus() != null ? mapStatusToResponse(history.getOldStatus()) : null)
                .newStatus(mapStatusToResponse(history.getNewStatus()))
                .changedBy(mapUserToResponse(history.getChangedBy()))
                .build();
    }
}
