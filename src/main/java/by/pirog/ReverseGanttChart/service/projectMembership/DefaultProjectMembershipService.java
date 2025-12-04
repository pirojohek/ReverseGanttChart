package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ResponseUserMembershipMeDto;
import by.pirog.ReverseGanttChart.exception.*;
import by.pirog.ReverseGanttChart.security.enums.UserRole;
import by.pirog.ReverseGanttChart.security.token.DualPreAuthenticatedAuthenticationToken;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.service.project.ProjectEntityService;
import by.pirog.ReverseGanttChart.service.user.UserService;
import by.pirog.ReverseGanttChart.storage.entity.ProjectEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectUserRoleEntity;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectMembershipRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectUserRoleRepository;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProjectMembershipService implements GetProjectMembershipByUserEmailAndProjectId, MembershipService {

    private final UserService userService;
    private final ProjectEntityService projectService;

    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;

    @Override
    public Optional<ProjectMembershipEntity> findProjectMembershipByUserEmailAndProjectId(String email, Long projectId) {
        return projectMembershipRepository.findByUserEmailAndProjectId(email, projectId);
    }

    @Override
    public void addMembershipToProjectByEmail(AddProjectMembershipDto dto) {
        if (dto.userRole().equals(UserRole.ADMIN)) {
            throw new ActionDenied("You cant add any administrator to this project");
        }

        var token =
                (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserEntity userEntity = userService.findUserByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException("User with email " + dto.email() + " not found"));
        // Todo обработать ошибку
        ProjectEntity projectEntity = projectService.findProjectById(token.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + token.getProjectId() + " not found"));
        // Todo обработать ошибку
        ProjectUserRoleEntity userRole = projectUserRoleRepository
                .findProjectUserRoleEntityByRoleName(dto.userRole().getAuthority())
                .orElseThrow(() -> new RoleNotFoundException("Role with name " + dto.userRole() + " not found"));

        ProjectMembershipEntity projectMembershipEntity = ProjectMembershipEntity.builder()
                .project(projectEntity)
                .user(userEntity)
                .userRole(userRole)
                .build();
        this.projectMembershipRepository.save(projectMembershipEntity);

    }

    @Override
    public void removeMembershipFromProjectByEmail(String email) {
        var token =
                (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectMembershipEntity membership =
                this.projectMembershipRepository.findByUserEmailAndProjectId(email, token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + email + " not found"));

        this.projectMembershipRepository.delete(membership);
    }

    @Override
    public List<InfoProjectMembershipDto> findAllMembershipByProjectId() {
        var token =
                (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        List<ProjectMembershipEntity> projectMembershipEntities =
                this.projectMembershipRepository.findAllByProjectId(token.getProjectId());

        return projectMembershipEntities.stream().map(entity -> InfoProjectMembershipDto.builder()
                .email(entity.getUser().getEmail())
                .userRole(entity.getUserRole().getRoleName())
                .projectId(entity.getProject().getId())
                .build()).toList();
    }

    @Override
    public void updateProjectMembershipAuthority(String email, UserRole userRole) {
        var token =
                (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ProjectMembershipEntity projectMembership =
                this.projectMembershipRepository.findByUserEmailAndProjectId(email, token.getProjectId())
                        .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + email + " not found"));

        ProjectUserRoleEntity role = this.projectUserRoleRepository.findProjectUserRoleEntityByRoleName(userRole.getAuthority())
                .orElseThrow(() -> new RoleNotFoundException("Role with name " + userRole.getAuthority() + " not found"));

        projectMembership.setUserRole(role);
        this.projectMembershipRepository.save(projectMembership);
    }

    @Override
    public ResponseUserMembershipMeDto getInfoAboutCurrentMembership() {
        var token = (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ProjectMembershipEntity entity = findProjectMembershipByUserEmailAndProjectId(user.getEmail(), token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + user.getEmail() + " not found"));


        return ResponseUserMembershipMeDto.builder()
                .email(user.getEmail())
                .projectId(token.getProjectId())
                .role(entity.getUserRole().getRoleName())
                .build();

    }

    @Override
    public List<ResponseUserMembershipMeDto> getAllUserMemberships() {

        var userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ProjectMembershipEntity> projectMemberships = this.projectMembershipRepository
                .findAllByUserEmail(userDetails.getEmail());

        return projectMemberships.stream().map(entity -> ResponseUserMembershipMeDto.builder()
                .email(entity.getUser().getEmail())
                .projectId(entity.getProject().getId())
                .role(entity.getUserRole().getRoleName())
                .projectDescription(entity.getProject().getProjectDescription())
                .projectName(entity.getProject().getProjectName())
                        .build()).toList();
    }

    @Override
    public ProjectMembershipEntity getCurrentProjectMembership() {
        var token = (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return findProjectMembershipByUserEmailAndProjectId(user.getEmail(), token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + user.getEmail() + " not found"));
    }

    @Override
    public ProjectMembershipEntity getProjectMembershipByEmail(String email) {
        var token = (DualPreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        return this.projectMembershipRepository.findByUserEmailAndProjectId(email, token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + email + " not found"));
    }

}
