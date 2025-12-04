package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreateProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreatedProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.ProjectComponentResponseDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.taskMakerDto.TaskMakerResponseDto;
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
import java.util.List;

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

        var customUserDetails = (CustomUserDetails) authentication.getPrincipal();

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
    public ProjectComponentResponseDto getProjectComponentById(Long componentId) {
        return null;
    }

    @Override
    public ProjectComponentResponseDto getProjectComponentByProjectIdWithHierarchy(Long componentId) {
        return null;
    }


    private List<TaskMakerResponseDto> getTaskMakersAsDto(List<TaskMakerEntity> taskMakerEntities) {
        return null;
    }

    private List<ProjectComponentResponseDto> getProjectComponentsAsDto(List<ProjectComponentEntity> projectComponentEntities) {
        return null;
    }

    private StudentTaskStatusResponseDto getStudentTaskStatusAsDto(StudentTaskStatusEntity studentTaskStatusEntity) {
        return null;
    }

    private ReviewerTaskStatusResponseDto getReviewerTaskStatusAsDto(ReviewerTaskStatusEntity reviewerTaskStatusEntity) {
        return null;
    }

}
