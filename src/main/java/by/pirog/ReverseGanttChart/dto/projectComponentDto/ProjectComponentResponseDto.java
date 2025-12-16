package by.pirog.ReverseGanttChart.dto.projectComponentDto;


import by.pirog.ReverseGanttChart.dto.commentDto.CommentResponseDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.taskMakerDto.TaskMakerResponseDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectComponentResponseDto {
    private Long id;

    private String title;
    private String description;
    private Long projectId;
    private Long parentId;
    private LocalDateTime createdAt;

    private String globalTaskStatus;

    private LocalDateTime deadline;

    private LocalDateTime startDate;

    private InfoProjectMembershipDto creator;

    private List<ProjectComponentResponseDto> children;

    private StudentTaskStatusResponseDto taskStatus;

    private ReviewerTaskStatusResponseDto reviewerTaskStatus;

    private List<TaskMakerResponseDto> taskMakers;

    private List<CommentResponseDto> comments;
}
