package by.pirog.ReverseGanttChart.security.factory;

import by.pirog.ReverseGanttChart.security.enums.TokenType;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public class BasicTokenCookieFactory implements Function<Authentication, Token> {

    private Duration tokenTtl = Duration.ofDays(1);

    @Override
    public Token apply(Authentication authentication) {
        var now = Instant.now();

        return new Token(UUID.randomUUID(), authentication.getName(),
                List.of(), null, TokenType.NONE, now, now.plus(tokenTtl));
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }
}
