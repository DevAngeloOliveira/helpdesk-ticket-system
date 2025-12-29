package io.github.angelo.TicketingSystem.service;

import io.github.angelo.TicketingSystem.dto.request.CommentRequest;
import io.github.angelo.TicketingSystem.dto.response.CommentResponse;
import io.github.angelo.TicketingSystem.dto.response.UserResponse;
import io.github.angelo.TicketingSystem.exception.ResourceNotFoundException;
import io.github.angelo.TicketingSystem.model.Comment;
import io.github.angelo.TicketingSystem.model.Ticket;
import io.github.angelo.TicketingSystem.model.User;
import io.github.angelo.TicketingSystem.repository.CommentRepository;
import io.github.angelo.TicketingSystem.repository.TicketRepository;
import io.github.angelo.TicketingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.getTicketId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Comment comment = Comment.builder()
                .message(request.getMessage())
                .ticket(ticket)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return mapToResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTicket(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }
        return commentRepository.findByTicketId(ticketId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .createdAt(comment.getCreatedAt())
                .ticketId(comment.getTicket().getId())
                .user(mapUserToResponse(comment.getUser()))
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
}
