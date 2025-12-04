package by.pirog.ReverseGanttChart.dto.commentDto;

public record CreateCommentDto(
        Long projectComponentId,
        String comment
) {
}
