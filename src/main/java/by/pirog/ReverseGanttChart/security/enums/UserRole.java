package by.pirog.ReverseGanttChart.security.enums;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    PLANNER("ROLE_PLANNER"),
    REVIEWER("ROLE_REVIEWER"),
    STUDENT("ROLE_STUDENT"),
    VIEWER("ROLE_VIEWER");

    private final String authority;
    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

}
