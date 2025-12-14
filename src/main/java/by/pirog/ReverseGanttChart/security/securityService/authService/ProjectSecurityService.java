package by.pirog.ReverseGanttChart.security.securityService.authService;

import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component("projectSecurityService")
public class ProjectSecurityService {

    private final RoleHierarchy roleHierarchy;

    public ProjectSecurityService(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    public boolean hasProjectAccess(Authentication authentication, HttpServletRequest request) {
        if (!(authentication instanceof CustomAuthenticationToken userToken)) {
            return false;
        }

        Long userProjectId = userToken.getProjectId();
        Long requestProjectId = Long.valueOf(request.getParameter("projectId"));

        if (userProjectId.equals(requestProjectId)) {
            return true;
        }
        return false;
    }


    public boolean hasProjectAccessWithRole(Authentication authentication, HttpServletRequest request,
                                            String role) {
        if (!(authentication instanceof CustomAuthenticationToken userToken)) {
            return false;
        }

        return hasProjectAccess(authentication, request) && userToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList().contains(role);
    }

    public boolean hasProjectAccessWithMinRole(Authentication authentication, HttpServletRequest request,
                                               String role) {
        if (!(authentication instanceof CustomAuthenticationToken userToken)) {
            return false;
        }

        boolean hasRoleAccess = userToken.getAuthorities().stream()
                .anyMatch(r -> hasRoleWithHierarchy(r.getAuthority(), role));
        return hasRoleAccess && hasProjectAccess(authentication, request);
    }

    private boolean hasRoleWithHierarchy(String userRole, String minRequiredRole) {
        Collection<? extends GrantedAuthority> reachableAuthorities =
                roleHierarchy.getReachableGrantedAuthorities(
                        List.of(new SimpleGrantedAuthority(userRole))
                );

        return reachableAuthorities.contains(new SimpleGrantedAuthority(minRequiredRole));
    }

}
