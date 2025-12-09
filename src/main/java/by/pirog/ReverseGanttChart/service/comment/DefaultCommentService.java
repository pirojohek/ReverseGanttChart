package by.pirog.ReverseGanttChart.service.comment;

import by.pirog.ReverseGanttChart.dto.commentDto.CreateCommentDto;
import by.pirog.ReverseGanttChart.dto.commentDto.CreatedCommentDto;
import by.pirog.ReverseGanttChart.exception.ProjectComponentNotFoundException;
import by.pirog.ReverseGanttChart.security.token.DualPreAuthenticatedAuthenticationToken;
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
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;

    // Todo - переделать эту штуку под сервис потом
    private final ProjectComponentRepository projectComponentRepository;

    private final MembershipService membershipService;

    @Override
    public CreatedCommentDto createComment(CreateCommentDto dto) {
        var token = (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ProjectMembershipEntity membership = this.membershipService.getCurrentProjectMembership();

        ProjectComponentEntity projectComponent = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(), dto.projectComponentId())
                .orElseThrow(() -> new ProjectComponentNotFoundException("Task not found"));

        CommentEntity comment = CommentEntity.builder()
                .commenter(membership)
                .projectComponent(projectComponent)
                .comment(dto.comment())
                .createdAt(Instant.now())
                .build();
        comment = this.commentRepository.save(comment);

        return CreatedCommentDto.builder()
                .commenter(this.membershipService.parseProjectMembershipDto(membership))
                .id(comment.getCommentId())
                .comment(comment.getComment())
                .taskId(projectComponent.getId())
                .createdAt(LocalDateTime.ofInstant(comment.getCreatedAt(), TimeZone.getDefault().toZoneId()))
                .build();
    }
}
