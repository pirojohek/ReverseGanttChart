package by.pirog.ReverseGanttChart.service.comment;

import by.pirog.ReverseGanttChart.dto.commentDto.CreateCommentDto;
import by.pirog.ReverseGanttChart.dto.commentDto.CreatedCommentDto;

public interface CommentService {

    CreatedCommentDto createComment(CreateCommentDto createCommentDto);
}
