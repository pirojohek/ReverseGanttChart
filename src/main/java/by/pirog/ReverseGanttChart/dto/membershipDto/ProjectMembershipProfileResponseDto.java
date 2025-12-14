package by.pirog.ReverseGanttChart.dto.membershipDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Builder
@Data
public class ProjectMembershipProfileResponseDto {
    private String email;
    private Long projectId;
    private String role;
    private String username;

    // Todo мб как то модифицировать статистикой
}
