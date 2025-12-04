package by.pirog.ReverseGanttChart.dto.commentDto;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    String comment;

    Long taskId;

    InfoProjectMembershipDto commenter;

    LocalDateTime createdAt;
}
