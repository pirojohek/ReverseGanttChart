package by.pirog.ReverseGanttChart.security.factory;

import by.pirog.ReverseGanttChart.enums.TokenType;
import by.pirog.ReverseGanttChart.security.token.Token;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public class AuthenticationTokenCookieFactory implements Function<Authentication, Token> {

    @Setter
    private Duration tokenTtl = Duration.ofDays(1);

    @Override
    public Token apply(Authentication authentication) {
        var now = Instant.now();

        return new Token(UUID.randomUUID(), authentication.getName(),
                List.of(), null, TokenType.NONE, now, now.plus(tokenTtl));
    }
}
