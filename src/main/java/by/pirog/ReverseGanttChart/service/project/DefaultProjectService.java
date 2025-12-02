package by.pirog.ReverseGanttChart.service.project;

import by.pirog.ReverseGanttChart.dto.projectDto.*;
import by.pirog.ReverseGanttChart.exception.DefaultServerException;
import by.pirog.ReverseGanttChart.exception.ProjectNotFoundException;
import by.pirog.ReverseGanttChart.security.token.DualPreAuthenticatedAuthenticationToken;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.storage.entity.ProjectEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectMembershipRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.rmi.UnexpectedException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;


@Service
@RequiredArgsConstructor
public class DefaultProjectService implements ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMembershipRepository projectMembershipRepository;

    private final ProjectUserRoleRepository projectUserRoleRepository;

    @Override
    public CreatedProjectDto createProject(CreateProjectDto projectDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        ProjectEntity project = projectRepository.save(ProjectEntity.builder()
                        .projectName(projectDto.projectName())
                        .projectOwner(customUserDetails.getUser())
                        .projectDescription(projectDto.description())
                        .createdAt(Instant.now())
                .build());
        project.setDeadlineFromLocalDate(projectDto.deadline());

        ProjectMembershipEntity projectMembershipEntity = projectMembershipRepository.save(ProjectMembershipEntity.builder()
                .project(project)
                .user(customUserDetails.getUser())
                .userRole(projectUserRoleRepository.findProjectUserRoleEntityByRoleName("ROLE_ADMIN")
                        .orElseThrow(() -> new DefaultServerException("Something went wrong")))
                .build());

        return CreatedProjectDto.builder()
                .projectDescription(project.getProjectDescription())
                .projectName(project.getProjectName())
                .projectOwnerEmail(project.getProjectOwner().getEmail())
                .createdAt(project.getCreatedAtAsLocalDate())
                .deadline(project.getDeadlineAsLocalDate())
                .updatedAt(LocalDate.now())
                .build();
    }

    @Override
    public void deleteProject() {
        var token = (DualPreAuthenticatedAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        this.projectRepository.deleteById(token.getProjectId());
    }

    @Override
    public UpdatedProjectDto updateProject(UpdateProjectDto dto) {
        var token = (DualPreAuthenticatedAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        ProjectEntity project = projectRepository.findById(token.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found " + token.getProjectId()));

        if (dto.deadline() != null) {
            project.setDeadlineFromLocalDate(dto.deadline());
        }
        if (dto.description() != null && !dto.description().isEmpty()) {
            project.setProjectDescription(dto.description());
        }
        if (dto.name() != null && !dto.name().isEmpty()) {
            project.setProjectName(dto.name());
        }
        this.projectRepository.save(project);

        return UpdatedProjectDto.builder()
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .deadline(project.getDeadlineAsLocalDate())
                .build();
    }

    @Override
    public ProjectInfoDto getProjectInfo() {
        var token = (DualPreAuthenticatedAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        var project = projectRepository.findById(token.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found " + token.getProjectId()));

        return ProjectInfoDto.builder()
                .projectName(project.getProjectName())
                .projectOwnerEmail(project.getProjectOwner().getEmail())
                .projectDescription(project.getProjectDescription())
                .deadline(project.getDeadlineAsLocalDate())
                .createdAt(project.getCreatedAtAsLocalDate())
                .updatedAt(project.getUpdatedAtAsLocalDate())
                .build();
    }
}
