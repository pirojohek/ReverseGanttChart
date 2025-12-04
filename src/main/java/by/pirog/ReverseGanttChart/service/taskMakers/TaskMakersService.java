package by.pirog.ReverseGanttChart.service.taskMakers;


import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;

import java.util.List;

public interface TaskMakersService {

    List<TakenTaskResponseDto> takeTasksToMake(Long taskId, Boolean subtasks);

    List<TakenTaskResponseDto> giveMembershipTasksToMake(String email, Long taskId, Boolean subtasks);

    List<TakenTaskResponseDto> tasksToMake(ProjectMembershipEntity membership, Long taskId, Boolean subtasks);
}