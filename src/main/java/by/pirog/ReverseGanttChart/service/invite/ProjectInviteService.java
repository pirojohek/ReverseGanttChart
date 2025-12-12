package by.pirog.ReverseGanttChart.service.invite;

import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.exception.*;
import by.pirog.ReverseGanttChart.service.email.EmailService;
import by.pirog.ReverseGanttChart.service.projectMembership.DefaultProjectMembershipService;
import by.pirog.ReverseGanttChart.service.projectMembership.GetProjectMembershipByUserEmailAndProjectId;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.ProjectInviteRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectRepository;
import by.pirog.ReverseGanttChart.storage.repository.ProjectUserRoleRepository;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectInviteService {

    private final ProjectInviteRepository projectInviteRepository;
    private final GetProjectMembershipByUserEmailAndProjectId getProjectMembershipByUserEmailAndProjectId;

    // Todo - переделать уже наконец то все под сервисы
    private final ProjectRepository projectRepository;
    private final MembershipService membershipService;
    private final UserRepository userRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;

    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private ProjectInviteEntity saveInvitationToDb(InviteRequestDto request) {

        if (!this.projectInviteRepository
                .findByEmailAndProjectId(request.email(), request.projectId()).isEmpty()) {
            throw new IllegalArgumentException("Invite already send");
        }

        if (this.getProjectMembershipByUserEmailAndProjectId.findProjectMembershipByUserEmailAndProjectId(request.email(), request.projectId()).isPresent()) {
            throw new IllegalArgumentException("User already in this project");
        }

        ProjectEntity project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        ProjectMembershipEntity sender = this.membershipService.getCurrentProjectMembership();

        UserEntity user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ProjectUserRoleEntity userRole = this.projectUserRoleRepository.findProjectUserRoleEntityByRoleName(request.role())
                .orElseThrow(() -> new RoleNotFoundException("Роль не найдена"));

        String token = UUID.randomUUID().toString();

        ProjectInviteEntity projectInvite = ProjectInviteEntity.builder()
                .project(project)
                .user(user)
                .userRole(userRole)
                .inviter(sender)
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


    public void acceptInvitation(String token) {
        ProjectInviteEntity invite = this.projectInviteRepository.findProjectInviteEntityByToken(token)
                .orElseThrow(() -> new InviteTokenException("Token not found or expired"));

        // Todo - добавить проверку истек токен или нет

        ProjectMembershipEntity projectMembership = ProjectMembershipEntity.builder()
                .project(invite.getProject())
                .user(invite.getUser())
                .userRole(invite.getUserRole())
                .build();
        this.membershipService.saveProjectMembership(projectMembership);
    }
}
