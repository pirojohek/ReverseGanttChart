package by.pirog.ReverseGanttChart.security.factory;

import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationManagerFactory {


    public WebExpressionAuthorizationManager hasProjectAccess() {
        return new WebExpressionAuthorizationManager(
                "@projectSecurityService.hasProjectAccess(authentication, request)"
        );
    }

    public WebExpressionAuthorizationManager hasProjectAccessWithRole(String role) {
        return new WebExpressionAuthorizationManager(
                "@projectSecurityService.hasProjectAccessWithRole(authentication, request)" +
                        "and hasRole('" + role + "')"
        );
    }
}
