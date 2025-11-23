package by.pirog.ReverseGanttChart.security.strategy;

import by.pirog.ReverseGanttChart.security.factory.BasicTokenCookieFactory;
import by.pirog.ReverseGanttChart.security.token.Token;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;


public class TokenCookieSessionAuthenticationStrategy implements SessionAuthenticationStrategy {

    @Setter
    Function<Authentication, Token> tokenCookieBasicFactory = new BasicTokenCookieFactory();

    @Setter
    private Function<Token, String> tokenStringSerializer = Objects::toString;

    @Override
    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request, HttpServletResponse response)
            throws SessionAuthenticationException {

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            var token = this.tokenCookieBasicFactory.apply(authentication);
            var tokenString = this.tokenStringSerializer.apply(token);

            var cookie = new Cookie("__Host-auth-token", tokenString);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setDomain(null);

            cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));

            response.addCookie(cookie);
        }
    }

    public TokenCookieSessionAuthenticationStrategy authenticationSerializer(
            Function<Token, String> tokenStringSerializer
    ){
        this.tokenStringSerializer = tokenStringSerializer;
        return this;
    }
}
