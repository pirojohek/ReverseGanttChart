package by.pirog.ReverseGanttChart.security.token;

import by.pirog.ReverseGanttChart.security.enums.TokenType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record Token(UUID id, String subject, List<String> authorities, Long projectId,
                    TokenType tokenType, Instant createdAt, Instant expiresAt) {
}
