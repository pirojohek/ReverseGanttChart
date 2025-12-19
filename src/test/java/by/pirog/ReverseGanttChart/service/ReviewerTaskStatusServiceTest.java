package by.pirog.ReverseGanttChart.service;

import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.SetReviewerTaskStatusRequestDto;
import by.pirog.ReverseGanttChart.events.event.ReviewerTaskStatusChangedEvent;
import by.pirog.ReverseGanttChart.mapper.ReviewerTaskStatusMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.service.reviewerTaskStatus.DefaultReviewerTaskStatusService;
import by.pirog.ReverseGanttChart.service.reviewerTaskStatus.ReviewerTaskStatusService;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import by.pirog.ReverseGanttChart.storage.repository.ReviewerStatusRepository;
import by.pirog.ReverseGanttChart.storage.repository.ReviewerTaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewerTaskStatusServiceTest {

    @Mock
    private ReviewerTaskStatusRepository reviewerTaskStatusRepository;
    @Mock
    private ReviewerStatusRepository reviewerStatusRepository;
    @Mock
    private ProjectComponentRepository projectComponentRepository;
    @Mock
    private MembershipService membershipService;
    @Mock
    private ReviewerTaskStatusMapper reviewerTaskStatusMapper;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private DefaultReviewerTaskStatusService reviewerTaskStatusService;

    @BeforeEach
    public void setUp() {
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

    private UserEntity testUser() {
        return UserEntity.builder()
                .id(1L)
                .email("test@mail.ru")
                .password("password")
                .username("testUser")
                .build();
    }

    private ProjectEntity testProject() {
        return ProjectEntity.builder()
                .id(10L)
                .projectName("test-project")
                .build();
    }

    private ProjectUserRoleEntity testProjectUserRole() {
        return ProjectUserRoleEntity.builder()
                .id(1L)
                .roleName("PLANNER")
                .build();
    }

    private ProjectMembershipEntity testProjectMembership() {
        return ProjectMembershipEntity.builder()
                .id(1L)
                .user(testUser())
                .project(testProject())
                .userRole(testProjectUserRole())
                .projectUsername("test-user")
                .build();
    }

    private ProjectComponentEntity testProjectComponent(Long componentId) {
        return ProjectComponentEntity.builder()
                .id(componentId)
                .project(testProject())
                .build();
    }

    private ReviewerStatusEntity testReviewerStatus() {
        return ReviewerStatusEntity.builder()
                .id(1L)
                .statusName("Accepted")
                .build();
    }

    @Test
    void setReviewerTaskStatus_ReturnsReviewerTaskStatusDto_ReviewerTaskStatusDoesNotExist() {
        // given
        Long projectId = 10L;
        Long taskId = 1L;
        Long reviewerTaskStatusId = 100L;
        mockSecurityContext(projectId);
        var membership = testProjectMembership();
        var projectComponent = testProjectComponent(taskId);
        var status = testReviewerStatus();

        ReviewerTaskStatusResponseDto responseDto = mock(ReviewerTaskStatusResponseDto.class);

        SetReviewerTaskStatusRequestDto dto = SetReviewerTaskStatusRequestDto.builder()
                .taskId(taskId)
                .status("Accepted")
                .build();


        when(reviewerTaskStatusMapper.toReviewerTaskStatusResponseDto(any()))
                .thenReturn(responseDto);

        when(membershipService.getCurrentProjectMembership())
                .thenReturn(membership);
        when(projectComponentRepository.findProjectComponentEntityByProjectIdAndComponentId(projectId, dto.taskId()))
                .thenReturn(Optional.of(projectComponent));
        when(reviewerStatusRepository.findReviewerStatusEntityByStatusName(dto.status()))
                .thenReturn(Optional.of(status));
        when(reviewerTaskStatusRepository.findReviewerTaskStatusEntitiesByProjectComponentId(taskId))
                .thenReturn(Optional.empty());

        when(reviewerTaskStatusRepository.save(any(ReviewerTaskStatusEntity.class)))
                .thenAnswer(invocation -> {
                    ReviewerTaskStatusEntity entity = invocation.getArgument(0);
                    entity.setId(reviewerTaskStatusId);
                    return entity;
                });

        //when
        ReviewerTaskStatusResponseDto result = reviewerTaskStatusService.setReviewerTaskStatus(dto);

        // then
        assertSame(result, responseDto);

        verify(reviewerTaskStatusRepository, times(1))
                .save(argThat(entity ->
                        entity.getTaskStatus() == status &&
                                entity.getTask() == projectComponent &&
                                entity.getProjectMembership() == membership

                ));

        verify(reviewerTaskStatusMapper, times(1))
                .toReviewerTaskStatusResponseDto(argThat(entity ->
                        entity.getProjectMembership() == membership &&
                                entity.getTaskStatus() == status &&
                                entity.getTask() == projectComponent));

        verify(applicationEventPublisher).publishEvent(
                argThat((Object event) -> {
                            if (!(event instanceof ReviewerTaskStatusChangedEvent e)) {
                                return false;
                            }
                            return e.taskId().equals(taskId)
                                    && e.newStatus() == status;
                        }
                )
        );
    }


    @Test
    void setReviewerTaskStatus_returnsReviewerTaskStatusDto_ReviewerTaskStatusExist() {
        // given
        Long projectId = 10L;
        Long taskId = 1L;
        Long reviewerTaskStatusId = 100L;

        mockSecurityContext(projectId);

        var membership = testProjectMembership();
        var projectComponent = testProjectComponent(taskId);
        var reviewerTaskStatus = testReviewerStatus();

        var dto = SetReviewerTaskStatusRequestDto.builder()
                .taskId(taskId)
                .status("Accepted")
                .build();

        var reviewerTaskStatusEntity = ReviewerTaskStatusEntity.builder()
                .id(reviewerTaskStatusId)
                .projectMembership(testProjectMembership())
                .task(projectComponent)
                .taskStatus(testReviewerStatus())
                .build();

        var responseDto = mock(ReviewerTaskStatusResponseDto.class);

        when(membershipService.getCurrentProjectMembership())
                .thenReturn(membership);

        when(projectComponentRepository.findProjectComponentEntityByProjectIdAndComponentId(projectId, dto.taskId()))
                .thenReturn(Optional.of(projectComponent));

        when(reviewerStatusRepository.findReviewerStatusEntityByStatusName(dto.status()))
                .thenReturn(Optional.of(reviewerTaskStatus));

        when(reviewerTaskStatusRepository.findReviewerTaskStatusEntitiesByProjectComponentId(dto.taskId()))
                .thenReturn(Optional.of(reviewerTaskStatusEntity));

        when(reviewerTaskStatusRepository.save(any(ReviewerTaskStatusEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(reviewerTaskStatusMapper.toReviewerTaskStatusResponseDto(any()))
                .thenReturn(responseDto);

        // when

        var result =  reviewerTaskStatusService.setReviewerTaskStatus(dto);

        // then

        assertSame(result, responseDto);

        verify(reviewerTaskStatusRepository, times(1)).save(
                argThat(entity ->
                        entity.getTaskStatus() == reviewerTaskStatus
                && entity.getTask() == projectComponent
                && entity.getProjectMembership() == membership)
        );

        verify(reviewerTaskStatusMapper, times(1))
                .toReviewerTaskStatusResponseDto(argThat(entity ->
                        entity.getProjectMembership() == membership &&
                        entity.getTaskStatus() == reviewerTaskStatus &&
                        entity.getTask() == projectComponent)
                );

        verify(applicationEventPublisher, times(1)).publishEvent(
                argThat((Object entity) -> {
                    if (!(entity instanceof ReviewerTaskStatusChangedEvent e)){
                        return false;
                    }

                    return e.taskId().equals(taskId)
                            && e.newStatus() == reviewerTaskStatus;
                })
        );

    }

}
