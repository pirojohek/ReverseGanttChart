package by.pirog.ReverseGanttChart.service.project;

import by.pirog.ReverseGanttChart.dto.projectDto.CreateProjectDto;
import by.pirog.ReverseGanttChart.dto.projectDto.CreatedProjectDto;
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
                .build());

        ProjectMembershipEntity projectMembershipEntity = projectMembershipRepository.save(ProjectMembershipEntity.builder()
                .project(project)
                .user(customUserDetails.getUser())
                .userRole(projectUserRoleRepository.findProjectUserRoleEntityByRoleName("ROLE_ADMIN"))
                .build());

        return CreatedProjectDto.builder()
                .projectDescription(project.getProjectDescription())
                .projectName(project.getProjectName())
                .projectOwnerEmail(project.getProjectOwner().getEmail())
                .build();
    }
}
