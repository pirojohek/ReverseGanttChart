package by.pirog.ReverseGanttChart.security.factory;

import by.pirog.ReverseGanttChart.security.enums.TokenType;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ProjectTokenCookieFactory implements Function<ProjectMembershipEntity, Token> {


    private Duration tokenTtl = Duration.ofDays(1);

    @Override
    public Token apply(ProjectMembershipEntity projectMembership) {
        var now = Instant.now();

        return new Token(UUID.randomUUID(), projectMembership.getUser().getEmail(),
                List.of(projectMembership.getUserRole().getRoleName()), projectMembership.getProject().getId(),
                TokenType.PROJECT, now, now.plus(tokenTtl));
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

}
