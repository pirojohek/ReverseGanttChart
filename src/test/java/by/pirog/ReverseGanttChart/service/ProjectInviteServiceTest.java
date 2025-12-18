package by.pirog.ReverseGanttChart.service;

import by.pirog.ReverseGanttChart.dto.invite.ChangeInviteRoleDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteResponseDto;
import by.pirog.ReverseGanttChart.enums.InviteStatus;
import by.pirog.ReverseGanttChart.enums.TokenType;
import by.pirog.ReverseGanttChart.exception.CannotSendInviteException;
import by.pirog.ReverseGanttChart.exception.UserNotFoundException;
import by.pirog.ReverseGanttChart.mapper.InviteMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.service.email.EmailService;
import by.pirog.ReverseGanttChart.service.invite.ProjectInviteService;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectInviteServiceTest {

    @Mock
    private ProjectInviteRepository projectInviteRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMembershipRepository projectMembershipRepository;
    @Mock
    private ProjectUserRoleRepository projectUserRoleRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private MembershipService membershipService;
    @Mock
    private InviteMapper inviteMapper;


    @InjectMocks
    private ProjectInviteService projectInviteService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(Long projectId) {
        Authentication baseAuth = mock(Authentication.class);
        Token token = new Token(
                UUID.randomUUID(),
                "test-user",
                List.of(),
                projectId,
                null,
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(baseAuth.getCredentials()).thenReturn(token);
        when(baseAuth.getAuthorities()).thenReturn(List.of());

        CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(baseAuth);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(customAuthenticationToken);

        SecurityContextHolder.setContext(context);
    }

    private UserEntity testUser(){
        return UserEntity.builder()
                .id(1L)
                .email("test@mail.ru")
                .password("password")
                .username("testUser")
                .build();
    }

    private ProjectEntity testProject(){
        return ProjectEntity.builder()
                .id(10L)
                .projectName("test-project")
                .build();
    }

    private ProjectUserRoleEntity testProjectUserRole(){
        return ProjectUserRoleEntity.builder()
                .id(1L)
                .roleName("PLANNER")
                .build();
    }

    private ProjectMembershipEntity testProjectMembership(){
        return ProjectMembershipEntity.builder()
                .id(1L)
                .user(testUser())
                .project(testProject())
                .userRole(testProjectUserRole())
                .projectUsername("test-user")
                .build();
    }

    private ProjectInviteEntity testProjectInvite(){
        return ProjectInviteEntity.builder()
                .id(1L)
                .user(testUser())
                .project(testProject())
                .userRole(testProjectUserRole())
                .inviter(testProjectMembership())
                .inviteStatus(InviteStatus.SUBMITTED)
                .token("test-token")
                .build();
    }

    @Test
    void saveInvitationToDb_returnsProjectInviteEntity(){
        var dto = new InviteRequestDto("test@mail.ru", 10L, "PLANNER");

        var testUser = testUser();
        var testProject = testProject();
        var inviter = testProjectMembership();
        var projectUserRole = testProjectUserRole();

         var savedInvite = ProjectInviteEntity.builder()
                 .id(1L)
                 .user(testUser)
                 .project(testProject)
                 .userRole(testProjectUserRole())
                 .inviter(inviter)
                 .inviteStatus(InviteStatus.SUBMITTED)
                 .build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(testUser));
        when(projectInviteRepository.findByEmailAndProjectId(dto.email(), dto.projectId()))
                .thenReturn(Optional.empty());
        when(projectMembershipRepository.findProjectMembershipByUserEmailAndProjectId(dto.email(), dto.projectId()))
                .thenReturn(Optional.empty());
        when(projectRepository.findById(dto.projectId())).thenReturn(Optional.of(testProject));
        when(membershipService.getCurrentProjectMembership()).thenReturn(inviter);

        when(projectUserRoleRepository.findProjectUserRoleEntityByRoleName(dto.role())).thenReturn(Optional.of(projectUserRole));

        when(projectInviteRepository.save(any(ProjectInviteEntity.class))).thenReturn(savedInvite);

        var responseDto = mock(InviteResponseDto.class);
        when(inviteMapper.projectInviteEntityToDto(savedInvite)).thenReturn(responseDto);

        InviteResponseDto result = projectInviteService.sendInvitation(dto);

        assertSame(responseDto, result);
        verify(projectInviteRepository, times(1)).save(argThat(invite ->
                        invite.getUser().getId().equals(testUser.getId()) &&
                                invite.getProject().getId().equals(testProject.getId()) &&
                                invite.getInviter().getId().equals(inviter.getId()) &&
                                invite.getInviteStatus() == InviteStatus.SUBMITTED &&
                                invite.getUserRole().getRoleName().equals(projectUserRole.getRoleName()) &&
                                invite.getToken() != null
                ));
        verify(emailService, times(1)).sendInvitationEmail(
                eq(dto.email()),
                eq(testProject.getProjectName()),
                contains("/accept-invite?token="),
                eq(inviter.getUser().getEmail())
        );
    }

    @Test
    void sendInvitation_userNotFound_throwUserNotFoundException(){
        var dto = new InviteRequestDto("unknown@mail.ru", 10L, "PLANNER");

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        var exception = assertThrows(UserNotFoundException.class, () ->
                projectInviteService.sendInvitation(dto));

        assertEquals("User not found with email: " + dto.email(), exception.getMessage());
        verifyNoInteractions(projectInviteRepository, emailService);
    }

    @Test
    void sendInvitation_inviteAlreadySend_throwsCannotSendInviteException(){
        var dto = new InviteRequestDto("test@mail.ru", 10L, "PLANNER");
        var user = testUser();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(projectInviteRepository.findByEmailAndProjectId(dto.email(), dto.projectId()))
                .thenReturn(Optional.of(new ProjectInviteEntity()));

        var exception = assertThrows(CannotSendInviteException.class, () ->
                projectInviteService.sendInvitation(dto));

        assertEquals("Invite already send", exception.getMessage());
        verify(projectMembershipRepository, never())
                .findProjectMembershipByUserEmailAndProjectId(dto.email(), dto.projectId());
        verifyNoInteractions(emailService);
    }

    @Test
    void sendInvitation_projectMembershipFound_throwsCannotSendInviteException(){
        var dto = new InviteRequestDto("test@mail.ru", 10L, "PLANNER");
        var user = testUser();
        var projectMembership = testProjectMembership();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(projectInviteRepository.findByEmailAndProjectId(dto.email(), dto.projectId()))
                .thenReturn(Optional.empty());
        when(projectMembershipRepository.findProjectMembershipByUserEmailAndProjectId(dto.email(), dto.projectId()))
                .thenReturn(Optional.of(projectMembership));

        var exception = assertThrows(CannotSendInviteException.class, () ->
                projectInviteService.sendInvitation(dto));

        assertEquals("User already in this project", exception.getMessage());
        verify(projectInviteRepository, never()).save(any(ProjectInviteEntity.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void getAllInvitesInProject_success_returnsListOfInvites(){
        Long projectId = 10L;

        mockSecurityContext(projectId);

        var invite1 = testProjectInvite();
        var invite2 = testProjectInvite();

        List<ProjectInviteEntity> invites = List.of(invite1, invite2);
        List<InviteResponseDto> responseDtos = List.of(
                mock(InviteResponseDto.class),
                mock(InviteResponseDto.class)
        );

        when(projectInviteRepository.findAllByProjectId(projectId)).thenReturn(invites);
        when(inviteMapper.listEntitiesToListResponseDto(invites)).thenReturn(responseDtos);

        List<InviteResponseDto> result = this.projectInviteService.getAllInvitesInProject();

        assertSame(responseDtos, result);

        verify(projectInviteRepository, times(1))
                .findAllByProjectId(projectId);

        verify(inviteMapper, times(1))
                .listEntitiesToListResponseDto(invites);

    }

    @Test
    void resendInvite_success_returnsInviteResponseDtoAndSendNewInvite(){
        // Given
        Long projectId = 10L;
        String token = "oldToken";
        String email = "test@mail.ru";

        mockSecurityContext(projectId);

        var invite = testProjectInvite();

        invite.setToken(token);
        invite.setInviteStatus(InviteStatus.EXPIRED);

        var inviter = testProjectMembership();

        // When
        when(projectInviteRepository.findByEmailAndProjectId(invite.getUser().getEmail(), projectId))
                .thenReturn(Optional.of(invite));
        when(membershipService.getCurrentProjectMembership()).thenReturn(inviter);

        var responseDto = mock(InviteResponseDto.class);
        when(inviteMapper.projectInviteEntityToDto(invite)).thenReturn(responseDto);


        // Then
        InviteResponseDto result = projectInviteService.resendInvite(invite.getUser().getEmail());

        assertSame(responseDto, result);
        assertEquals(InviteStatus.SUBMITTED, invite.getInviteStatus());
        assertNotNull(invite.getCreatedAt());
        assertNotNull(invite.getExpiredAt());
        assertNotNull(invite.getToken());
        assertNotEquals(token, invite.getToken());
        assertSame(inviter, invite.getInviter());

        String inviteUrl = "/accept-invite?token=";

        verify(projectInviteRepository, times(1)).save(invite);

        verify(emailService, times(1)).sendInvitationEmail(
                eq(email),
                eq(invite.getProject().getProjectName()),
                contains(inviteUrl),
                eq(inviter.getUser().getEmail())

        );
    }

    @Test
    void changeInviteRole_success_returnsInviteResponseDtoAndSaveNewInviteWithNewRole(){
        // given
        ChangeInviteRoleDto dto = ChangeInviteRoleDto.builder()
                .email("test@mail.ru")
                .role("STUDENT")
                .build();
        Long projectId = 10L;
        var invite = testProjectInvite();
        var inviter = testProjectMembership();
        var userRole = testProjectUserRole();
        // when
        mockSecurityContext(projectId);

        when(projectInviteRepository.findByEmailAndProjectId(dto.getEmail(), projectId))
                .thenReturn(Optional.of(invite));
        when(projectUserRoleRepository.findProjectUserRoleEntityByRoleName(dto.getRole()))
                .thenReturn(Optional.of(userRole));
        when(membershipService.getCurrentProjectMembership()).thenReturn(inviter);

        var responseDto = mock(InviteResponseDto.class);
        when(inviteMapper.projectInviteEntityToDto(invite))
                .thenReturn(responseDto);

        // then
        var result = projectInviteService.changeInviteRole(dto);

        assertSame(responseDto, result);

        assertEquals(InviteStatus.SUBMITTED, invite.getInviteStatus());
        assertNotNull(invite.getCreatedAt());
        assertNotNull(invite.getExpiredAt());
        assertNotNull(invite.getToken());
        assertSame(inviter, invite.getInviter());
        assertSame(userRole, invite.getUserRole());

        verify(projectInviteRepository, times(1)).save(invite);

        verify(inviteMapper, times(1))
                .projectInviteEntityToDto(invite);

    }


    @Test
    void deleteInvite_success(){
        // given
        Long projectId = 10L;
        String email = "test@mail.ru";

        var invite = testProjectInvite();

        mockSecurityContext(projectId);

        // when
        when(projectInviteRepository.findByEmailAndProjectId(email, projectId))
                .thenReturn(Optional.of(invite));
        // then
        projectInviteService.deleteInvite(email);
        verify(projectInviteRepository, times(1)).delete(invite);

    }

    @Test
    void acceptInvite_success(){
        // given
        String token = "token";
        var invite = testProjectInvite();

        // when
        when(projectInviteRepository.findProjectInviteEntityByToken(token))
                .thenReturn(Optional.of(invite));
        when(projectMembershipRepository.findProjectMembershipByUsernameAndProjectId
                (invite.getUser().getUsername(), invite.getProject().getId()))
                .thenReturn(Optional.empty());



    }
}
