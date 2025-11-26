package by.pirog.ReverseGanttChart.security.strategy;

import by.pirog.ReverseGanttChart.security.securityService.tokenService.ResponseCookieUserAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;


public class TokenCookieSessionAuthenticationStrategy implements SessionAuthenticationStrategy {

    private ResponseCookieUserAuthenticationToken cookieService;

    public TokenCookieSessionAuthenticationStrategy(ResponseCookieUserAuthenticationToken cookieService) {
        this.cookieService = cookieService;
    }

    @Override
    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request, HttpServletResponse response)
            throws SessionAuthenticationException {

        if (authentication instanceof UsernamePasswordAuthenticationToken) {

            var cookie = cookieService.createCookieUserAuthentication(authentication);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }


    public TokenCookieSessionAuthenticationStrategy cookieService(ResponseCookieUserAuthenticationToken cookieService) {
        this.cookieService = cookieService;
        return this;
    }
}
