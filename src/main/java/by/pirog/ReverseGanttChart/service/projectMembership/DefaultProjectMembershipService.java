package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipUserProjectsResponseDto;
import by.pirog.ReverseGanttChart.exception.*;
import by.pirog.ReverseGanttChart.enums.UserRole;
import by.pirog.ReverseGanttChart.mapper.ProjectMembershipMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.service.project.ProjectEntityService;
import by.pirog.ReverseGanttChart.service.user.UserService;
import by.pirog.ReverseGanttChart.storage.entity.ProjectEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectUserRoleEntity;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectMembershipRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProjectMembershipService implements GetProjectMembershipByUsernameAndProjectId, MembershipService {

    private final UserService userService;
    private final ProjectEntityService projectService;

    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;


    private final ProjectMembershipMapper projectMembershipMapper;

    @Override
    public Optional<ProjectMembershipEntity> findProjectMembershipByUsernameAndProjectId(String username, Long projectId) {
        return projectMembershipRepository.findByUsernameAndProjectId(username, projectId);
    }

    @Override
    public void addMembershipToProjectByEmail(AddProjectMembershipDto dto) {
        if (dto.userRole().equals(UserRole.ADMIN.getAuthority())) {
            throw new ActionDenied("You cant add any administrator to this project");
        }

        var token =
                (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (this.projectMembershipRepository
                .findProjectMembershipByUsernameAndProjectId(dto.username(), token.getProjectId()).isPresent()) {
            throw new IllegalArgumentException("User with username " + dto.username() + " already exists");
        }

        UserEntity userEntity = userService.findUserByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException("User with email " + dto.email() + " not found"));

        ProjectEntity projectEntity = projectService.findProjectById(token.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + token.getProjectId() + " not found"));

        ProjectUserRoleEntity userRole = projectUserRoleRepository
                .findProjectUserRoleEntityByRoleName(dto.userRole())
                .orElseThrow(() -> new RoleNotFoundException("Role with name " + dto.userRole() + " not found"));

        ProjectMembershipEntity projectMembershipEntity = ProjectMembershipEntity.builder()
                .project(projectEntity)
                .user(userEntity)
                .userRole(userRole)
                .projectUsername(dto.username())
                .build();
        this.projectMembershipRepository.save(projectMembershipEntity);
    }
    // Todo - когда добавили пользователя по email, уже по факту все операции с ним будут происходить по внутреннему нику
    @Override
    public void removeMembershipFromProjectByEmail(String projectUsername) {
        var token =
                (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        // Todo - здесь должен быть другой метод, который уже ищет по нику внутри проекта
        ProjectMembershipEntity membership =
                this.projectMembershipRepository.findProjectMembershipByProjectUsernameAndProjectId(projectUsername, token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with project username " + projectUsername + " not found"));

        this.projectMembershipRepository.delete(membership);
    }

    @Override
    public List<InfoProjectMembershipDto> findAllMembershipByProjectId() {
        var token =
                (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        List<ProjectMembershipEntity> projectMembershipEntities =
                this.projectMembershipRepository.findAllByProjectId(token.getProjectId());

        return projectMembershipEntities.stream()
                .map(this.projectMembershipMapper::toInfoProjectMembershipDto)
                .toList();
    }

    @Override
    public void updateProjectMembershipAuthority(String projectUsername, UserRole userRole) {
        var token =
                (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ProjectMembershipEntity projectMembership =
                this.projectMembershipRepository.findProjectMembershipByProjectUsernameAndProjectId(projectUsername, token.getProjectId())
                        .orElseThrow(() -> new UserIsNotMemberInProjectException("User with project username " + projectUsername + " not found"));

        ProjectUserRoleEntity role = this.projectUserRoleRepository.findProjectUserRoleEntityByRoleName(userRole.getAuthority())
                .orElseThrow(() -> new RoleNotFoundException("Role with name " + userRole.getAuthority() + " not found"));

        projectMembership.setUserRole(role);
        this.projectMembershipRepository.save(projectMembership);
    }

    @Override
    public List<ProjectMembershipUserProjectsResponseDto> getAllUserMemberships() {

        var userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

        List<ProjectMembershipEntity> projectMemberships = this.projectMembershipRepository
                .findAllByUserEmail(userDetails.getEmail());

        return projectMemberships.stream()
                .map(this.projectMembershipMapper::toProjectMembershipUserProjectsResponseDto)
                .toList();
    }

    @Override
    public ProjectMembershipEntity getCurrentProjectMembership() {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return findProjectMembershipByUsernameAndProjectId(user.getUsername(), token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + user.getEmail() + " not found"));
    }

    @Override
    public ProjectMembershipEntity getProjectMembershipByEmail(String email) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        return this.projectMembershipRepository.findProjectMembershipByUserEmailAndProjectId(email, token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + email + " not found"));
    }

    @Override
    public void saveProjectMembership(ProjectMembershipEntity projectMembershipEntity) {
        this.projectMembershipRepository.save(projectMembershipEntity);
    }

}
