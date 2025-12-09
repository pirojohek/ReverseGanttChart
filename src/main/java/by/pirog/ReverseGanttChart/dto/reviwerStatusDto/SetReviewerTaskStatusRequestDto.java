package by.pirog.ReverseGanttChart.dto.reviwerStatusDto;


import lombok.Builder;

@Builder
public record SetReviewerTaskStatusRequestDto(
        Long taskId,
        String status
) {

}
