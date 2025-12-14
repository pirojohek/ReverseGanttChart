package by.pirog.ReverseGanttChart.security.securityService.tokenService;

import by.pirog.ReverseGanttChart.configuration.TokenCookieNameProperties;
import by.pirog.ReverseGanttChart.security.factory.AuthenticationTokenCookieFactory;
import by.pirog.ReverseGanttChart.security.factory.ProjectTokenCookieFactory;

import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;

@Service
public class TokenService implements ResponseCookieProjectToken, ResponseCookieUserAuthenticationToken{

    private Function<Token, String> tokenStringSerializer = Objects::toString;

    Function<Authentication, Token> authenticationTokenFactory = new AuthenticationTokenCookieFactory();

    Function<ProjectMembershipEntity, Token> projectTokenFactory = new ProjectTokenCookieFactory();

    private final TokenCookieNameProperties tokenCookieNameProperties;

    public TokenService(Function<Token, String> tokenStringSerializer, TokenCookieNameProperties tokenCookieNameProperties) {
        this.tokenStringSerializer = tokenStringSerializer;
        this.tokenCookieNameProperties = tokenCookieNameProperties;
    }

    @Override
    public ResponseCookie createProjectTokenCookie(ProjectMembershipEntity projectMembershipEntity) {
        var projectTokenCookie = this.projectTokenFactory.apply(projectMembershipEntity);
        var projectTokenSerialized = this.tokenStringSerializer.apply(projectTokenCookie);

        return ResponseCookie.from(tokenCookieNameProperties.getAuthCookieName(), projectTokenSerialized)
                .httpOnly(true)
                .path("/")
                .maxAge((int) ChronoUnit.SECONDS.between(Instant.now(), projectTokenCookie.expiresAt()))
                .domain(null)
                .build();
    }

    @Override
    public ResponseCookie createCookieUserAuthentication(Authentication authentication) {
        var authenticationToken = this.authenticationTokenFactory.apply(authentication);
        var authenticationTokenSerialized = this.tokenStringSerializer.apply(authenticationToken);

        return ResponseCookie.from(tokenCookieNameProperties.getAuthCookieName(), authenticationTokenSerialized)
                .httpOnly(true)
                .path("/")
                .maxAge((int) ChronoUnit.SECONDS.between(Instant.now(), authenticationToken.expiresAt()))
                .domain(null)
                .build();
    }

    public TokenService tokenStringSerializer(Function<Token, String> tokenStringSerializer) {
        this.tokenStringSerializer = tokenStringSerializer;
        return this;
    }
}
