package io.github.angelo.TicketingSystem.service;

import io.github.angelo.TicketingSystem.dto.request.PriorityRequest;
import io.github.angelo.TicketingSystem.dto.response.PriorityResponse;
import io.github.angelo.TicketingSystem.exception.DuplicateResourceException;
import io.github.angelo.TicketingSystem.exception.ResourceNotFoundException;
import io.github.angelo.TicketingSystem.model.Priority;
import io.github.angelo.TicketingSystem.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriorityService {

    private final PriorityRepository priorityRepository;

    @Transactional
    public PriorityResponse createPriority(PriorityRequest request) {
        if (priorityRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Priority already exists: " + request.getName());
        }

        Priority priority = Priority.builder()
                .name(request.getName())
                .level(request.getLevel())
                .build();

        Priority savedPriority = priorityRepository.save(priority);
        return mapToResponse(savedPriority);
    }

    @Transactional(readOnly = true)
    public PriorityResponse getPriorityById(Long id) {
        Priority priority = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found with id: " + id));
        return mapToResponse(priority);
    }

    @Transactional(readOnly = true)
    public List<PriorityResponse> getAllPriorities() {
        return priorityRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PriorityResponse updatePriority(Long id, PriorityRequest request) {
        Priority priority = priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found with id: " + id));

        if (!priority.getName().equals(request.getName()) && priorityRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Priority already exists: " + request.getName());
        }

        priority.setName(request.getName());
        priority.setLevel(request.getLevel());
        Priority updatedPriority = priorityRepository.save(priority);
        return mapToResponse(updatedPriority);
    }

    @Transactional
    public void deletePriority(Long id) {
        if (!priorityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Priority not found with id: " + id);
        }
        priorityRepository.deleteById(id);
    }

    private PriorityResponse mapToResponse(Priority priority) {
        return PriorityResponse.builder()
                .id(priority.getId())
                .name(priority.getName())
                .level(priority.getLevel())
                .build();
    }
}
