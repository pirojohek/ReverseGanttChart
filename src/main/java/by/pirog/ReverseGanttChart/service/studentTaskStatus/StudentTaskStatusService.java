package by.pirog.ReverseGanttChart.service.studentTaskStatus;

import by.pirog.ReverseGanttChart.dto.studentStatusDto.SetTaskStatusRequestDto;
import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;

public interface StudentTaskStatusService {
    StudentTaskStatusResponseDto setTaskStatus(SetTaskStatusRequestDto dto);
}
