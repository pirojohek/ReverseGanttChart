package by.pirog.ReverseGanttChart.security.factory;

import by.pirog.ReverseGanttChart.security.enums.UserRole;
import by.pirog.ReverseGanttChart.security.securityService.authService.ProjectSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationManagerFactory {

    private final ProjectSecurityService projectSecurityService;

    public AuthorizationManager<RequestAuthorizationContext> hasProjectAccess() {
        return (authentication, context) -> {
            boolean granted = projectSecurityService.hasProjectAccess(
                    authentication.get(),
                    context.getRequest()
            );
            return new AuthorizationDecision(granted);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> hasProjectAccessWithRole(String role) {
        return (authentication, context) -> {
            boolean granted = projectSecurityService.hasProjectAccessWithRole(
                    authentication.get(),
                    context.getRequest(),
                    role
            );
            return new AuthorizationDecision(granted);
        };
    }

    public AuthorizationManager<RequestAuthorizationContext> hasProjectAccessWithMinRole(String minRole) {
        return (authentication, context) -> {
            boolean granted = projectSecurityService.hasProjectAccessWithMinRole(
                    authentication.get(),
                    context.getRequest(),
                    minRole
            );
            return new AuthorizationDecision(granted);
        };
    }
}
