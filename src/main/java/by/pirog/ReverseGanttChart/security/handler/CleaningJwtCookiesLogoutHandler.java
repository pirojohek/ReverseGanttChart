package by.pirog.ReverseGanttChart.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;


public class CleaningJwtCookiesLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createExpiredJwtCookie("__Host-auth-token").toString());

        response.addHeader(HttpHeaders.SET_COOKIE,
                createExpiredJwtCookie("__Host-project-token").toString());

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
