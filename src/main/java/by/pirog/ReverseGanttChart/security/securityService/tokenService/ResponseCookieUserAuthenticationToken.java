package by.pirog.ReverseGanttChart.security.securityService.tokenService;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface ResponseCookieUserAuthenticationToken {
    ResponseCookie createCookieUserAuthentication(Authentication authentication);
}
