package by.pirog.ReverseGanttChart.dto.membershipDto;

import by.pirog.ReverseGanttChart.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatedProjectMembershipResponse {
    private String email;

    private UserRole role;
}
