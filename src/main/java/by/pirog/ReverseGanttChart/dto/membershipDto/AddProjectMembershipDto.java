package by.pirog.ReverseGanttChart.dto.membershipDto;

import by.pirog.ReverseGanttChart.security.enums.UserRole;

// Todo нужно как то обезопасить с userRole
public record AddProjectMembershipDto(
        String email,
        String userRole
) {
}
