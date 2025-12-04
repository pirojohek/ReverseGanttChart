package by.pirog.ReverseGanttChart.service.taskMakers;


import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;

import java.util.List;

public interface TaskMakersService {

    List<TakenTaskResponseDto> takeTasksToMake(Long taskId, Boolean subtasks);
}
