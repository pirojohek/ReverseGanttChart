package by.pirog.ReverseGanttChart.security;

import by.pirog.ReverseGanttChart.configuration.TokenCookieNameProperties;
import by.pirog.ReverseGanttChart.security.securityService.blacklistService.RedisTokenBlacklistService;
import by.pirog.ReverseGanttChart.security.token.Token;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthenticationCookieAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, Token> tokenCookieStringDeserializer;

    private final RedisTokenBlacklistService tokenBlacklistService;

    private final TokenCookieNameProperties tokenCookieNameProperties;

    public AuthenticationCookieAuthenticationConverter(Function<String, Token> tokenCookieStringDeserializer,
                                                       RedisTokenBlacklistService tokenBlacklistService,
                                                       TokenCookieNameProperties tokenCookieNameProperties) {
        this.tokenCookieStringDeserializer = tokenCookieStringDeserializer;
        this.tokenBlacklistService = tokenBlacklistService;
        this.tokenCookieNameProperties = tokenCookieNameProperties;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        boolean isAuthenticationTokenInBlacklist = checkAuthenticationTokenInBlacklist(request);
        Authentication authenticationAuth = extractAuthenticationTokenToAuth(request);


        if (authenticationAuth != null && !isAuthenticationTokenInBlacklist) {
            return new PreAuthenticatedAuthenticationToken
                    (authenticationAuth.getPrincipal(), authenticationAuth.getCredentials(),
                            authenticationAuth.getAuthorities());
        }
        return null;
    }

     private boolean checkAuthenticationTokenInBlacklist(HttpServletRequest request) {
        return tokenBlacklistService.isAuthTokenBlacklisted
                (getCookieValue(request, tokenCookieNameProperties.getAuthCookieName()));
     }


    private Authentication extractAuthenticationTokenToAuth(HttpServletRequest request){
        String tokenString = getCookieValue(request, tokenCookieNameProperties.getAuthCookieName());
        if (tokenString != null){
            var token =  tokenCookieStringDeserializer.apply(tokenString);
            return createAuthenticationToken(token);
        }
        return null;
    }

    private Authentication createAuthenticationToken(Token token) {
        return new PreAuthenticatedAuthenticationToken(token.subject(), token,
                token.authorities().stream()
                        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList()));
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie ->  cookie.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
