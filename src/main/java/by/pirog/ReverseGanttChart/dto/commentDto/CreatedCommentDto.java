package by.pirog.ReverseGanttChart.dto.commentDto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class CreatedCommentDto {
    private String comment;

    private Instant createdAt;
}
