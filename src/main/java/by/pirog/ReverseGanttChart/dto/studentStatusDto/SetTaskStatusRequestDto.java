package by.pirog.ReverseGanttChart.dto.studentStatusDto;


import lombok.Builder;

@Builder
public record SetTaskStatusRequestDto(
        Long taskId,
        String status
) {
}
