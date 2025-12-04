package by.pirog.ReverseGanttChart.dto.studentStatusDto;


import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentTaskStatusResponseDto {
    private InfoProjectMembershipDto student;

    private Long taskId;

    private String status;
}
