package by.pirog.ReverseGanttChart.dto.membershipDto;

// Todo нужно как то обезопасить с userRole
public record AddProjectMembershipDto(
        String email,
        String userRole,
        String username
) {
}
