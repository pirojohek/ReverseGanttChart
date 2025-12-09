package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.commentDto.CreateCommentDto;
import by.pirog.ReverseGanttChart.dto.commentDto.CreatedCommentDto;
import by.pirog.ReverseGanttChart.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment/")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CreatedCommentDto> createComment(@RequestBody CreateCommentDto createCommentDto) {
        CreatedCommentDto createdCommentDto = commentService.createComment(createCommentDto);
        return ResponseEntity.ok(createdCommentDto);
    }
}
