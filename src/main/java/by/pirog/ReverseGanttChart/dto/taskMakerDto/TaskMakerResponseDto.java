package by.pirog.ReverseGanttChart.dto.taskMakerDto;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskMakerResponseDto {
    private Long id;
    private Long taskId;

    private InfoProjectMembershipDto taskMakerInfo;
}
