package by.pirog.ReverseGanttChart.service.comment;

import by.pirog.ReverseGanttChart.dto.commentDto.CreateCommentDto;
import by.pirog.ReverseGanttChart.dto.commentDto.CreatedCommentDto;
import by.pirog.ReverseGanttChart.exception.ProjectComponentNotFoundException;
import by.pirog.ReverseGanttChart.mapper.CommentMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.storage.entity.CommentEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.repository.CommentRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;

    private final ProjectComponentRepository projectComponentRepository;

    private final MembershipService membershipService;

    private final CommentMapper commentMapper;

    @Override
    public CreatedCommentDto createComment(CreateCommentDto dto) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ProjectMembershipEntity membership = this.membershipService.getCurrentProjectMembership();

        ProjectComponentEntity projectComponent = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(), dto.projectComponentId())
                .orElseThrow(() -> new ProjectComponentNotFoundException("Task not found"));

        Instant createdAt = Instant.now();

        CommentEntity comment = CommentEntity.builder()
                .commenter(membership)
                .projectComponent(projectComponent)
                .comment(dto.comment())
                .createdAt(createdAt)
                .build();
        comment = this.commentRepository.save(comment);

        return this.commentMapper.toCreatedCommentDto(comment);
    }
}
