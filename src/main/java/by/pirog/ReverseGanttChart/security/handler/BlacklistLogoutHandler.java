package by.pirog.ReverseGanttChart.security.handler;

import by.pirog.ReverseGanttChart.configuration.TokenCookieNameProperties;
import by.pirog.ReverseGanttChart.security.securityService.blacklistService.RedisTokenBlacklistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class BlacklistLogoutHandler implements LogoutHandler {

    private final RedisTokenBlacklistService blacklistService;
    private final TokenCookieNameProperties tokenCookieNameProperties;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try{
            invalidateJwtTokens(request, response);

            log.info("Токены добавлены в blacklist");
        } catch (Exception e){
            log.error("Токены не добавлены в blacklist");
        }

    }

    private void invalidateJwtTokens(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String authToken = getCookieValue(request, this.tokenCookieNameProperties.getAuthCookieName());

        if (authToken != null){
            blacklistService.addToBlackListAuthToken(authToken);
        } else {
            log.warn("Auth token не найден в запросе на logout");
        }

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
