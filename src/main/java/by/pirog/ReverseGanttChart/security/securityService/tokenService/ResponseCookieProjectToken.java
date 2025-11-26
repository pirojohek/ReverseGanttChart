package by.pirog.ReverseGanttChart.security.securityService.tokenService;

import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import org.springframework.http.ResponseCookie;

@FunctionalInterface
public interface ResponseCookieProjectToken {
    ResponseCookie createProjectTokenCookie(ProjectMembershipEntity projectMembershipEntity);
}
