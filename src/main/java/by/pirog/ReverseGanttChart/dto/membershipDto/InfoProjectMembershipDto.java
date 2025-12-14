package by.pirog.ReverseGanttChart.dto.membershipDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoProjectMembershipDto {
    private String email;
    private String userRole;
    private Long projectId;
    private String username;
}
