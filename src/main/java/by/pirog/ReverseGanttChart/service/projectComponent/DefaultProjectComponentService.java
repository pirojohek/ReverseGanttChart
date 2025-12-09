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
        entity.setStartDateFromLocalDate(dto.startDate());

        entity = this.projectComponentRepository.save(entity);

        return CreatedProjectComponentDto.builder()
                .id(entity.getId())
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
    public List<ProjectComponentResponseDto> getProjectComponentsByProjectIdWithHierarchyOrderedByDate() {
        var token = (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        List<ProjectComponentEntity> rootComponents = this.projectComponentRepository
                .findRootComponentsByProjectId(token.getProjectId());


        return rootComponents.stream()
                .map(this::buildHierarchy)
                .toList();

    }

    private ProjectComponentResponseDto buildHierarchy(ProjectComponentEntity projectComponentEntity) {
        ProjectComponentResponseDto dto = getProjectComponentAsDto(projectComponentEntity);

        List<ProjectComponentEntity> children = projectComponentRepository.findChildrenByParentId(projectComponentEntity.getId());

        if (!children.isEmpty()) {
            List<ProjectComponentResponseDto> childDtos = children.stream()
                    .map(this::buildHierarchy)
                    .toList();

            dto = ProjectComponentResponseDto.builder()
                    .id(projectComponentEntity.getId())
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .projectId(dto.getProjectId())
                    .parentId(dto.getParentId())
                    .createdDate(dto.getCreatedDate())
                    .pos(dto.getPos())
                    .creator(dto.getCreator())
                    .taskStatus(dto.getTaskStatus())
                    .reviewerTaskStatus(dto.getReviewerTaskStatus())
                    .taskMakers(dto.getTaskMakers())
                    .comments(dto.getComments())
                    .children(childDtos)
                    .build();
        }
        return dto;
    }


    private ProjectComponentResponseDto getProjectComponentAsDto(ProjectComponentEntity entity) {

        return ProjectComponentResponseDto.builder()
                .id(entity.getId())
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
        if (taskMakerEntities == null || taskMakerEntities.isEmpty()) {
            return new ArrayList<>();
        }


        return taskMakerEntities.stream()
                .map(entity -> TaskMakerResponseDto.builder()
                        .id(entity.getId())
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

        if (studentTaskStatusEntity == null) {
            return null;
        }
        return StudentTaskStatusResponseDto.builder()
                .id(studentTaskStatusEntity.getId())
                .student(getProjectMembershipAsDto(studentTaskStatusEntity.getStudent()))
                .taskId(studentTaskStatusEntity.getTask().getId())
                .status(studentTaskStatusEntity.getStatus().getStatusName())
                .build();
    }

    private ReviewerTaskStatusResponseDto getReviewerTaskStatusAsDto(ReviewerTaskStatusEntity reviewerTaskStatusEntity) {
        if (reviewerTaskStatusEntity == null) {
            return null;
        }
        return ReviewerTaskStatusResponseDto.builder()
                .id(reviewerTaskStatusEntity.getId())
                .reviewer(getProjectMembershipAsDto(reviewerTaskStatusEntity.getProjectMembership()))
                .taskId(reviewerTaskStatusEntity.getTask().getId())
                .status(reviewerTaskStatusEntity.getTaskStatus().getStatusName())
                .build();
    }

    private List<CommentResponseDto> getCommentsAsDto(Set<CommentEntity> commentEntities) {
        if (commentEntities == null || commentEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return commentEntities.stream().map(entity ->
                CommentResponseDto.builder()
                        .id(entity.getCommentId())
                        .comment(entity.getComment())
                        .commenter(getProjectMembershipAsDto(entity.getCommenter()))
                        .createdAt(LocalDateTime.ofInstant(entity.getCreatedAt(), TimeZone.getDefault().toZoneId()))
                        .taskId(entity.getProjectComponent().getId())
                        .build())
                .toList();
    }

    private InfoProjectMembershipDto getProjectMembershipAsDto(ProjectMembershipEntity projectMembershipEntity) {
        // Todo оптимизировать
        if (projectMembershipEntity == null) {
            return null;
        }
        return InfoProjectMembershipDto.builder()
                .email(projectMembershipEntity.getUser().getEmail())
                .userRole(projectMembershipEntity.getUserRole().getRoleName())
                .projectId(projectMembershipEntity.getProject().getId())
                .build();
    }

}
