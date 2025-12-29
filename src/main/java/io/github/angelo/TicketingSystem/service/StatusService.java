package io.github.angelo.TicketingSystem.service;

import io.github.angelo.TicketingSystem.dto.request.StatusRequest;
import io.github.angelo.TicketingSystem.dto.response.StatusResponse;
import io.github.angelo.TicketingSystem.exception.DuplicateResourceException;
import io.github.angelo.TicketingSystem.exception.ResourceNotFoundException;
import io.github.angelo.TicketingSystem.model.Status;
import io.github.angelo.TicketingSystem.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    @Transactional
    public StatusResponse createStatus(StatusRequest request) {
        if (statusRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Status already exists: " + request.getName());
        }

        Status status = Status.builder()
                .name(request.getName())
                .build();

        Status savedStatus = statusRepository.save(status);
        return mapToResponse(savedStatus);
    }

    @Transactional(readOnly = true)
    public StatusResponse getStatusById(Long id) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with id: " + id));
        return mapToResponse(status);
    }

    @Transactional(readOnly = true)
    public List<StatusResponse> getAllStatuses() {
        return statusRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StatusResponse updateStatus(Long id, StatusRequest request) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with id: " + id));

        if (!status.getName().equals(request.getName()) && statusRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Status already exists: " + request.getName());
        }

        status.setName(request.getName());
        Status updatedStatus = statusRepository.save(status);
        return mapToResponse(updatedStatus);
    }

    @Transactional
    public void deleteStatus(Long id) {
        if (!statusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Status not found with id: " + id);
        }
        statusRepository.deleteById(id);
    }

    private StatusResponse mapToResponse(Status status) {
        return StatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .build();
    }
}
