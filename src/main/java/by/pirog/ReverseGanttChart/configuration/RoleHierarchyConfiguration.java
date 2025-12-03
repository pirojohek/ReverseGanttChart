package by.pirog.ReverseGanttChart.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfiguration {
    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = """
                ROLE_ADMIN > ROLE_PLANNER
                ROLE_PLANNER > ROLE_REVIEWER
                ROLE_REVIEWER > ROLE_STUDENT
                ROLE_STUDENT > ROLE_VIEWER
                """;
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
