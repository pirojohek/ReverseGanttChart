package by.pirog.ReverseGanttChart.security;

import by.pirog.ReverseGanttChart.security.token.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TokenCookieAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, Token> tokenCookieStringDeserializer;

    public TokenCookieAuthenticationConverter(Function<String, Token> tokenCookieStringDeserializer) {
        this.tokenCookieStringDeserializer = tokenCookieStringDeserializer;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("__Host-auth-token"))
                    .findFirst()
                    .map(cookie -> {
                        var token = tokenCookieStringDeserializer.apply(cookie.getValue());

                        return new PreAuthenticatedAuthenticationToken(token.subject(), token,
                                token.authorities().stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList()));
                    }).orElse(null);
        }
        return null;
    }
}
