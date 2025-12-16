package by.pirog.ReverseGanttChart.security.filter;

import by.pirog.ReverseGanttChart.exception.UserIsNotMemberInProjectException;
import by.pirog.ReverseGanttChart.security.securityService.tokenService.ResponseCookieProjectToken;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.service.projectMembership.GetProjectMembershipByUsernameAndProjectId;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
public class LoginIntoProjectCookieAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher =
            new AntPathRequestMatcher("/api/loginIntoProject/*", HttpMethod.POST.name());

    private final GetProjectMembershipByUsernameAndProjectId getProjectMembershipByUsernameAndProjectId;
    private final ResponseCookieProjectToken responseCookieProjectToken;

    public LoginIntoProjectCookieAuthenticationFilter(
            GetProjectMembershipByUsernameAndProjectId getProjectMembershipByUsernameAndProjectId,
            ResponseCookieProjectToken responseCookieProjectToken) {
        this.getProjectMembershipByUsernameAndProjectId = getProjectMembershipByUsernameAndProjectId;
        this.responseCookieProjectToken = responseCookieProjectToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long projectId = extractProjectId(request);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (!(authentication instanceof PreAuthenticatedAuthenticationToken)) {
                sendUnauthorizedError(response, "User not authenticated with token");
                return;
            }

            Token token = (Token) authentication.getCredentials();

            ProjectMembershipEntity projectMembershipEntity = getProjectMembershipByUsernameAndProjectId
                    .findProjectMembershipByUsernameAndProjectId(token.subject(), projectId)
                    .orElseThrow(() -> new UserIsNotMemberInProjectException("User is not member of project"));

            var cookie = responseCookieProjectToken.createProjectTokenCookie(projectMembershipEntity);
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Successfully logged into project\"}");

        } catch (UserIsNotMemberInProjectException e) {
            sendUnauthorizedError(response, e.getMessage());
        } catch (NumberFormatException e) {
            sendBadRequestError(response, "Invalid project ID format");
        } catch (Exception e) {
            sendInternalServerError(response, "Internal server error: " + e.getMessage());
        }

    }

    private Long extractProjectId(HttpServletRequest request) {
        String path = request.getRequestURI();
        String projectIdStr = path.substring("/api/loginIntoProject/".length());
        return Long.parseLong(projectIdStr);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }

    private void sendBadRequestError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Bad Request\", \"message\": \"" + message + "\"}");
    }

    private void sendInternalServerError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Internal Server Error\", \"message\": \"" + message + "\"}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Дополнительные условия если нужно
        return false;
    }
}