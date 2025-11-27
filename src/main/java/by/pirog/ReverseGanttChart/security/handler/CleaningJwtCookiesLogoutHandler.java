package by.pirog.ReverseGanttChart.security.handler;

import by.pirog.ReverseGanttChart.configuration.TokenCookieNameProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;


@RequiredArgsConstructor
public class CleaningJwtCookiesLogoutHandler implements LogoutHandler {

    private final TokenCookieNameProperties tokenCookieNameProperties;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createExpiredJwtCookie(this.tokenCookieNameProperties.getAuthCookieName()).toString());

        response.addHeader(HttpHeaders.SET_COOKIE,
                createExpiredJwtCookie(this.tokenCookieNameProperties.getProjectCookieName()).toString());

    }

    private ResponseCookie createExpiredJwtCookie(String cookieName){
        return ResponseCookie.from(cookieName, null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .domain(null)
                .build();
    }

}
