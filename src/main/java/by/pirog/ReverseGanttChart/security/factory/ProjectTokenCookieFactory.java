package by.pirog.ReverseGanttChart.security.factory;

import by.pirog.ReverseGanttChart.enums.TokenType;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ProjectTokenCookieFactory implements Function<ProjectMembershipEntity, Token> {

    @Setter
    private Duration tokenTtl = Duration.ofDays(1);

    @Override
    public Token apply(ProjectMembershipEntity projectMembership) {
        var now = Instant.now();

        return new Token(UUID.randomUUID(), projectMembership.getUser().getUsername(),
                List.of(projectMembership.getUserRole().getRoleName()), projectMembership.getProject().getId(),
                TokenType.PROJECT, now, now.plus(tokenTtl));
    }
}
