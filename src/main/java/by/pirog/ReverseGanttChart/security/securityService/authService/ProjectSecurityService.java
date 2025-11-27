package by.pirog.ReverseGanttChart.security.securityService.authService;

import by.pirog.ReverseGanttChart.security.token.DualPreAuthenticatedAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("projectSecurityService")
public class ProjectSecurityService {

    public boolean hasProjectAccess(Authentication authentication, HttpServletRequest request) {
        if (!(authentication instanceof DualPreAuthenticatedAuthenticationToken)) {
            return false;
        }

        var userToken =  (DualPreAuthenticatedAuthenticationToken) authentication;
        Long userProjectId = userToken.getProjectId();
        Long requestProjectId = Long.valueOf(request.getParameter("projectId"));

        if (userProjectId.equals(requestProjectId)) {
            return true;
        }
        return false;
    }
}
