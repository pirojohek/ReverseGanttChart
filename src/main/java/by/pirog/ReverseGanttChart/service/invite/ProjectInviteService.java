package by.pirog.ReverseGanttChart.service.invite;

import by.pirog.ReverseGanttChart.dto.invite.ChangeInviteRoleDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteResponseDto;
import by.pirog.ReverseGanttChart.enums.InviteStatus;
import by.pirog.ReverseGanttChart.exception.*;
import by.pirog.ReverseGanttChart.mapper.InviteMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.service.email.EmailService;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectInviteService {

    private final ProjectInviteRepository projectInviteRepository;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;
    private final ProjectMembershipRepository projectMembershipRepository;


    private final EmailService emailService;
    private final MembershipService membershipService;

    private final InviteMapper inviteMapper;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private Duration tokenTtl = Duration.ofDays(1);

    private ProjectInviteEntity saveInvitationToDb(InviteRequestDto request) {

        UserEntity user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!this.projectInviteRepository
                .findByEmailAndProjectId(request.email(), request.projectId()).isEmpty()) {
            throw new CannotSendInviteException("Invite already send");
        }
        if (this.projectMembershipRepository.findProjectMembershipByUserEmailAndProjectId(request.email(), request.projectId()).isPresent()) {
            throw new CannotSendInviteException("User already in this project");
        }

        ProjectEntity project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        ProjectMembershipEntity sender = this.membershipService.getCurrentProjectMembership();

        ProjectUserRoleEntity userRole = this.projectUserRoleRepository.findProjectUserRoleEntityByRoleName(request.role())
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        String token = UUID.randomUUID().toString();
        Instant now = Instant.now();
        // Todo - это тоже можно вынести в mapper, чтобы место не занимало
        ProjectInviteEntity projectInvite = ProjectInviteEntity.builder()
                .project(project)
                .user(user)
                .userRole(userRole)
                .inviter(sender)
                .inviteStatus(InviteStatus.SUBMITTED)
                .createdAt(now)
                .expiredAt(now.plus(tokenTtl))
                .token(token)
                .build();

        return projectInviteRepository.save(projectInvite);
    }

    public void sendInvitation(InviteRequestDto request) {
        ProjectInviteEntity projectInvite = saveInvitationToDb(request);

        String invitationUrl = frontendUrl + "/accept-invite?token=" + projectInvite.getToken();

        emailService.sendInvitationEmail(
                request.email(),
                projectInvite.getProject().getProjectName(),
                invitationUrl,
                projectInvite.getInviter().getUser().getEmail() // Todo - потом это все заменить на имя пользователя в системе
        );
    }

    // Todo - видимо нужно добавить еще кнопку отказа, вообще хотелось бы наверное сделать систему уведомлений

    public List<InviteResponseDto> getAllInvitesInProject(){
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        List<ProjectInviteEntity> projectInviteEntities =
                this.projectInviteRepository.findAllByProjectId(token.getProjectId());

        return this.inviteMapper.listEntitiesToListResponseDto(projectInviteEntities);
    }

    public InviteResponseDto resendInvite(String email) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectInviteEntity invite = this.projectInviteRepository.findByEmailAndProjectId(email, token.getProjectId())
                .orElseThrow(() -> new InviteNotFoundException("Invite not found"));

        Instant now = Instant.now();
        invite.setCreatedAt(now);
        invite.setExpiredAt(now.plus(tokenTtl));
        invite.setToken(UUID.randomUUID().toString());
        invite.setInviteStatus(InviteStatus.SUBMITTED);
        invite.setInviter(this.membershipService.getCurrentProjectMembership());

        this.projectInviteRepository.save(invite);

        String invitationUrl = frontendUrl + "/accept-invite?token=" + invite.getToken();

        emailService.sendInvitationEmail(
                email,
                invite.getProject().getProjectName(),
                invitationUrl,
                invite.getInviter().getUser().getEmail()
        );

        return inviteMapper.projectInviteEntityToDto(invite);
    }

    public InviteResponseDto changeInviteRole(ChangeInviteRoleDto dto) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectInviteEntity invite = this.projectInviteRepository.findByEmailAndProjectId(dto.getEmail(), token.getProjectId())
                .orElseThrow(() -> new InviteNotFoundException("Invite not found"));

        ProjectUserRoleEntity userRole = this.projectUserRoleRepository.findProjectUserRoleEntityByRoleName(dto.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        Instant now = Instant.now();
        invite.setCreatedAt(now);
        invite.setExpiredAt(now.plus(tokenTtl));
        invite.setInviteStatus(InviteStatus.SUBMITTED);
        invite.setInviter(this.membershipService.getCurrentProjectMembership());
        this.projectInviteRepository.save(invite);

        return inviteMapper.projectInviteEntityToDto(invite);
    }

    public void deleteInvite(String email) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectInviteEntity invite = this.projectInviteRepository.findByEmailAndProjectId(email, token.getProjectId())
                .orElseThrow(() -> new InviteNotFoundException("Invite not found"));

        this.projectInviteRepository.delete(invite);
    }

    public void acceptInvitation(String token, String username) {
        ProjectInviteEntity invite = this.projectInviteRepository.findProjectInviteEntityByToken(token)
                .orElseThrow(() -> new InviteTokenException("Token is expired"));

        if (this.projectMembershipRepository
                .findProjectMembershipByUsernameAndProjectId(username, invite.getProject().getId()).isPresent()) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        if (invite.getExpiredAt().isBefore(Instant.now())) {
            invite.setInviteStatus(InviteStatus.EXPIRED);
            this.projectInviteRepository.save(invite);
            throw new InviteTokenException("Token is expired");
        }

        ProjectMembershipEntity projectMembership = ProjectMembershipEntity.builder()
                .project(invite.getProject())
                .user(invite.getUser())
                .userRole(invite.getUserRole())
                .projectUsername(username)
                .build();
        this.membershipService.saveProjectMembership(projectMembership);
    }
}
