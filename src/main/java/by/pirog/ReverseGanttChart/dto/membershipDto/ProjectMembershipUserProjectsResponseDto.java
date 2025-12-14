package by.pirog.ReverseGanttChart.dto.membershipDto;

import by.pirog.ReverseGanttChart.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMembershipUserProjectsResponseDto {
    private String email;
    private Long projectId;
    private String role;

    private String username;

    private String projectName;

    private String projectDescription;

    private LocalDate deadline;

    private ProjectStatus projectStatus;
}
