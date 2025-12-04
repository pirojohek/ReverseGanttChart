package by.pirog.ReverseGanttChart.dto.taskMakerDto;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakenTaskResponseDto {

    InfoProjectMembershipDto taskMaker;

    Long taskId;

}
