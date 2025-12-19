package by.pirog.ReverseGanttChart.service.reviewerTaskStatus;


import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.SetReviewerTaskStatusRequestDto;
import by.pirog.ReverseGanttChart.events.event.ReviewerTaskStatusChangedEvent;
import by.pirog.ReverseGanttChart.exception.ProjectComponentNotFoundException;
import by.pirog.ReverseGanttChart.exception.ReviewerTaskStatusNotFound;
import by.pirog.ReverseGanttChart.mapper.ReviewerTaskStatusMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.entity.ReviewerStatusEntity;
import by.pirog.ReverseGanttChart.storage.entity.ReviewerTaskStatusEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import by.pirog.ReverseGanttChart.storage.repository.ReviewerStatusRepository;
import by.pirog.ReverseGanttChart.storage.repository.ReviewerTaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultReviewerTaskStatusService implements ReviewerTaskStatusService {

    private final ReviewerTaskStatusRepository reviewerTaskStatusRepository;
    private final ReviewerStatusRepository reviewerStatusRepository;

    private final ProjectComponentRepository projectComponentRepository;

    private final MembershipService membershipService;

    private final ReviewerTaskStatusMapper reviewerTaskStatusMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    // Todo - добавить проверку, что reviewer не может установить статус задачи, если нет статуса студента

    @Override
    public ReviewerTaskStatusResponseDto setReviewerTaskStatus(SetReviewerTaskStatusRequestDto dto) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ProjectMembershipEntity membership = this.membershipService.getCurrentProjectMembership();

        ProjectComponentEntity projectComponent = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(), dto.taskId())
                .orElseThrow(() -> new ProjectComponentNotFoundException("Task not found"));

        ReviewerStatusEntity taskStatus = this.reviewerStatusRepository.findReviewerStatusEntityByStatusName(dto.status())
                .orElseThrow(() -> new ReviewerTaskStatusNotFound("Status not found"));

        Optional<ReviewerTaskStatusEntity> existingStatusOpt = this.reviewerTaskStatusRepository
                .findReviewerTaskStatusEntitiesByProjectComponentId(dto.taskId());

        ReviewerTaskStatusEntity reviewerTaskStatusEntity;

        if (existingStatusOpt.isPresent()) {
            reviewerTaskStatusEntity = existingStatusOpt.get();
            reviewerTaskStatusEntity.setTaskStatus(taskStatus);
            reviewerTaskStatusEntity.setProjectMembership(membership);
        } else {
            reviewerTaskStatusEntity = ReviewerTaskStatusEntity.builder()
                    .projectMembership(membership)
                    .taskStatus(taskStatus)
                    .task(projectComponent)
                    .build();
        }

        reviewerTaskStatusEntity = this.reviewerTaskStatusRepository.save(reviewerTaskStatusEntity);

        applicationEventPublisher.publishEvent(new ReviewerTaskStatusChangedEvent(projectComponent.getId(),
                reviewerTaskStatusEntity.getTaskStatus()));

        return this.reviewerTaskStatusMapper.toReviewerTaskStatusResponseDto(reviewerTaskStatusEntity);
    }
}
