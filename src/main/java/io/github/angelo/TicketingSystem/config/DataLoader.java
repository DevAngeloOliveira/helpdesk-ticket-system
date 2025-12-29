package io.github.angelo.TicketingSystem.config;

import io.github.angelo.TicketingSystem.model.*;
import io.github.angelo.TicketingSystem.model.enums.UserRole;
import io.github.angelo.TicketingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Loading initial data...");
            loadInitialData();
            log.info("Initial data loaded successfully!");
        } else {
            log.info("Data already exists. Skipping initial load.");
        }
    }

    private void loadInitialData() {
        // Create Users
        User admin = User.builder()
                .name("Admin User")
                .email("admin@ticketsystem.com")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .build();

        User technician = User.builder()
                .name("John Technician")
                .email("tech@ticketsystem.com")
                .password(passwordEncoder.encode("tech123"))
                .role(UserRole.TECHNICIAN)
                .build();

        User user1 = User.builder()
                .name("Alice Johnson")
                .email("alice@example.com")
                .password(passwordEncoder.encode("user123"))
                .role(UserRole.USER)
                .build();

        User user2 = User.builder()
                .name("Bob Smith")
                .email("bob@example.com")
                .password(passwordEncoder.encode("user123"))
                .role(UserRole.USER)
                .build();

        List<User> users = userRepository.saveAll(Arrays.asList(admin, technician, user1, user2));
        log.info("Created {} users", users.size());

        // Create Statuses
        Status statusOpen = Status.builder().name("Open").build();
        Status statusInProgress = Status.builder().name("In Progress").build();
        Status statusResolved = Status.builder().name("Resolved").build();
        Status statusClosed = Status.builder().name("Closed").build();

        List<Status> statuses = statusRepository.saveAll(
                Arrays.asList(statusOpen, statusInProgress, statusResolved, statusClosed));
        log.info("Created {} statuses", statuses.size());

        // Create Priorities
        Priority priorityLow = Priority.builder().name("Low").level(3).build();
        Priority priorityMedium = Priority.builder().name("Medium").level(2).build();
        Priority priorityHigh = Priority.builder().name("High").level(1).build();
        Priority priorityCritical = Priority.builder().name("Critical").level(0).build();

        List<Priority> priorities = priorityRepository.saveAll(
                Arrays.asList(priorityLow, priorityMedium, priorityHigh, priorityCritical));
        log.info("Created {} priorities", priorities.size());

        // Create Categories
        Category categoryHardware = Category.builder()
                .name("Hardware")
                .description("Hardware related issues")
                .build();

        Category categorySoftware = Category.builder()
                .name("Software")
                .description("Software and application issues")
                .build();

        Category categoryNetwork = Category.builder()
                .name("Network")
                .description("Network and connectivity issues")
                .build();

        Category categoryAccess = Category.builder()
                .name("Access")
                .description("Account and access related requests")
                .build();

        List<Category> categories = categoryRepository.saveAll(
                Arrays.asList(categoryHardware, categorySoftware, categoryNetwork, categoryAccess));
        log.info("Created {} categories", categories.size());

        // Create Sample Tickets
        Ticket ticket1 = Ticket.builder()
                .title("Laptop not turning on")
                .description("My laptop won't start after the last update")
                .user(user1)
                .assignedTo(technician)
                .category(categoryHardware)
                .priority(priorityHigh)
                .status(statusInProgress)
                .build();

        Ticket ticket2 = Ticket.builder()
                .title("Cannot access email")
                .description("Getting authentication error when trying to login to email")
                .user(user2)
                .assignedTo(technician)
                .category(categoryAccess)
                .priority(priorityMedium)
                .status(statusOpen)
                .build();

        Ticket ticket3 = Ticket.builder()
                .title("Slow network connection")
                .description("Internet is very slow in the office")
                .user(user1)
                .category(categoryNetwork)
                .priority(priorityLow)
                .status(statusOpen)
                .build();

        List<Ticket> tickets = ticketRepository.saveAll(Arrays.asList(ticket1, ticket2, ticket3));
        log.info("Created {} tickets", tickets.size());

        // Create Status History for tickets
        StatusHistory history1 = StatusHistory.builder()
                .ticket(ticket1)
                .oldStatus(null)
                .newStatus(statusOpen)
                .changedBy(user1)
                .build();

        StatusHistory history2 = StatusHistory.builder()
                .ticket(ticket1)
                .oldStatus(statusOpen)
                .newStatus(statusInProgress)
                .changedBy(technician)
                .build();

        StatusHistory history3 = StatusHistory.builder()
                .ticket(ticket2)
                .oldStatus(null)
                .newStatus(statusOpen)
                .changedBy(user2)
                .build();

        StatusHistory history4 = StatusHistory.builder()
                .ticket(ticket3)
                .oldStatus(null)
                .newStatus(statusOpen)
                .changedBy(user1)
                .build();

        List<StatusHistory> histories = statusHistoryRepository.saveAll(
                Arrays.asList(history1, history2, history3, history4));
        log.info("Created {} status history records", histories.size());
    }
}
