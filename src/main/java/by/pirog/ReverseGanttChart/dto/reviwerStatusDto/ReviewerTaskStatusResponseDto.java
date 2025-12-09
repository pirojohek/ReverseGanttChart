package by.pirog.ReverseGanttChart.dto.reviwerStatusDto;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewerTaskStatusResponseDto {
    private Long id;

    private InfoProjectMembershipDto reviewer;

    private Long taskId;

    private String status;
}
