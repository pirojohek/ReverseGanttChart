package by.pirog.ReverseGanttChart.dto.commentDto;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Data
public class CreatedCommentDto {
    private Long id;

    String comment;

    Long taskId;

    InfoProjectMembershipDto commenter;

    LocalDateTime createdAt;
}
