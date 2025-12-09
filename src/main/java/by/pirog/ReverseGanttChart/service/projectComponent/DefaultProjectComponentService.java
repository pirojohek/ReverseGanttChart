package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.dto.commentDto.CommentResponseDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreateProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreatedProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.ProjectComponentResponseDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.taskMakerDto.TaskMakerResponseDto;
import by.pirog.ReverseGanttChart.exception.ProjectComponentNotFoundException;
import by.pirog.ReverseGanttChart.exception.ProjectComponentParentNotFound;
import by.pirog.ReverseGanttChart.exception.ProjectNotFoundException;
import by.pirog.ReverseGanttChart.exception.UserIsNotMemberInProjectException;
import by.pirog.ReverseGanttChart.security.token.DualPreAuthenticatedAuthenticationToken;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.service.project.ProjectEntityService;
import by.pirog.ReverseGanttChart.service.projectMembership.GetProjectMembershipByUserEmailAndProjectId;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class DefaultProjectComponentService implements ProjectComponentService{

    private final ProjectEntityService projectService;

    private final ProjectComponentRepository projectComponentRepository;

    private final GetProjectMembershipByUserEmailAndProjectId getProjectMembershipByUserEmailAndProjectId;

    @Override
    public CreatedProjectComponentDto createProjectComponent(CreateProjectComponentDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var token = (DualPreAuthenticatedAuthenticationToken) authentication;

        var customUserDetails = (CustomUserDetails) authentication.getDetails();

        ProjectMembershipEntity creator =
                getProjectMembershipByUserEmailAndProjectId.findProjectMembershipByUserEmailAndProjectId(customUserDetails.getEmail(), token.getProjectId())
                        .orElseThrow(() -> new UserIsNotMemberInProjectException(customUserDetails.getEmail()));

        ProjectComponentEntity parent = null;
        if (dto.parentId() != null) {
            parent = projectComponentRepository.
                    findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(),
                            dto.parentId())
                    .orElseThrow(() -> new ProjectComponentParentNotFound("parent id not found"));
        }

        ProjectEntity project = this.projectService.findProjectById(token.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(token.getProjectId().toString()));

        ProjectComponentEntity entity = ProjectComponentEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .creator(creator)
                .project(project)
                .projectComponentParent(parent)
                .createdAt(Instant.now())
                .build();
        entity.setDeadlineFromLocalDate(dto.deadline());

        this.projectComponentRepository.save(entity);

        return CreatedProjectComponentDto.builder()
                .title(entity.getTitle())
                .description(entity.getDescription())
                .parentId(entity.getProjectComponentParent() != null ? entity.getProjectComponentParent().getId(): null)
                .deadline(entity.getDeadlineAsLocalDate())
                .createdAt(entity.getDeadlineAsLocalDate())
                .creator(entity.getCreator().getUser().getEmail())
                .role(entity.getCreator().getUserRole().getRoleName())
                .build();
    }

    @Override
    public ProjectComponentResponseDto getProjectComponentByIdWithoutHierarchy(Long componentId) {

        var token = (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectComponentEntity entity = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentIdWithAllProperties(token.getProjectId(), componentId)
                .orElseThrow(() -> new ProjectComponentNotFoundException(componentId.toString()));

        return getProjectComponentAsDto(entity);
    }

    @Override
    public ProjectComponentResponseDto getProjectComponentByProjectIdWithHierarchy(Long componentId) {
        return null;
    }

    @Override
    public List<ProjectComponentResponseDto> getProjectComponentsByProjectIdWithHierarchyOrderedByDate() {
        return List.of();
    }

    private ProjectComponentResponseDto getProjectComponentAsDto(ProjectComponentEntity entity) {

        return ProjectComponentResponseDto.builder()
                .title(entity.getTitle())
                .description(entity.getDescription())
                .projectId(entity.getProject().getId())
                .parentId(entity.getProjectComponentParent() != null ? entity.getProjectComponentParent().getId(): null)
                .createdDate(LocalDateTime.ofInstant(entity.getCreatedAt(), TimeZone.getDefault().toZoneId()))
                .pos(entity.getPos())
                .children(new ArrayList<>())
                .creator(getProjectMembershipAsDto(entity.getCreator()))
                .taskStatus(getStudentTaskStatusAsDto(entity.getStudentTaskStatus()))
                .reviewerTaskStatus(getReviewerTaskStatusAsDto(entity.getReviewerTaskStatus()))
                .taskMakers(getTaskMakersAsDto(entity.getTaskMakers()))
                .comments(getCommentsAsDto(entity.getComments()))
                .build();
    }

    private List<TaskMakerResponseDto> getTaskMakersAsDto(Set<TaskMakerEntity> taskMakerEntities) {
        return taskMakerEntities.stream()
                .map(entity -> TaskMakerResponseDto.builder()
                        .taskMakerInfo(getProjectMembershipAsDto(entity.getMembership()))
                        .taskId(entity.getProjectComponent().getId())
                        .build())
                .toList();
    }

    private List<ProjectComponentResponseDto> getProjectComponentsAsDto(List<ProjectComponentEntity> projectComponentEntities) {
        return null;
    }

    private StudentTaskStatusResponseDto getStudentTaskStatusAsDto(StudentTaskStatusEntity studentTaskStatusEntity) {
        // Todo это тоже очень медленно выходит
        return StudentTaskStatusResponseDto.builder()
                .student(getProjectMembershipAsDto(studentTaskStatusEntity.getStudent()))
                .taskId(studentTaskStatusEntity.getTask().getId())
                .status(studentTaskStatusEntity.getStatus().getStatusName())
                .build();
    }

    private ReviewerTaskStatusResponseDto getReviewerTaskStatusAsDto(ReviewerTaskStatusEntity reviewerTaskStatusEntity) {
        return ReviewerTaskStatusResponseDto.builder()
                .reviewer(getProjectMembershipAsDto(reviewerTaskStatusEntity.getProjectMembership()))
                .taskId(reviewerTaskStatusEntity.getTask().getId())
                .status(reviewerTaskStatusEntity.getTaskStatus().getStatusName())
                .build();
    }

    private List<CommentResponseDto> getCommentsAsDto(Set<CommentEntity> commentEntities) {
        return commentEntities.stream().map(entity ->
                CommentResponseDto.builder()
                        .comment(entity.getComment())
                        .commenter(getProjectMembershipAsDto(entity.getCommenter()))
                        .createdAt(LocalDateTime.ofInstant(entity.getCreatedAt(), TimeZone.getDefault().toZoneId()))
                        .taskId(entity.getProjectComponent().getId())
                        .build())
                .toList();
    }

    private InfoProjectMembershipDto getProjectMembershipAsDto(ProjectMembershipEntity projectMembershipEntity) {
        // Todo оптимизировать
        return InfoProjectMembershipDto.builder()
                .email(projectMembershipEntity.getUser().getEmail())
                .userRole(projectMembershipEntity.getUserRole().getRoleName())
                .projectId(projectMembershipEntity.getProject().getId())
                .build();
    }

}
