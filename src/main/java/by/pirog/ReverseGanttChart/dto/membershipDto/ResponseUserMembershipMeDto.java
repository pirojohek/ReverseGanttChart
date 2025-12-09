package by.pirog.ReverseGanttChart.dto.membershipDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserMembershipMeDto {
    private String email;
    private Long projectId;
    private String role;

    private String projectName;

    private String projectDescription;

    private LocalDate deadline;
}
