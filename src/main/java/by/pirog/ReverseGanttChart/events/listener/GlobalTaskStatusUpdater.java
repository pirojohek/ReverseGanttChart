package by.pirog.ReverseGanttChart.events.listener;

import by.pirog.ReverseGanttChart.enums.GlobalTaskStatus;
import by.pirog.ReverseGanttChart.events.event.ReviewerTaskStatusChangedEvent;
import by.pirog.ReverseGanttChart.events.event.StudentTaskStatusChangedEvent;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.entity.ReviewerTaskStatusEntity;
import by.pirog.ReverseGanttChart.storage.entity.StudentTaskStatusEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GlobalTaskStatusUpdater {

    private final ProjectComponentRepository projectComponentRepository;

    @EventListener
    @Transactional
    public void updateGlobalStatusAfterChangedStudentTaskStatus(StudentTaskStatusChangedEvent event){
        ProjectComponentEntity entity = projectComponentRepository.findProjectComponentEntityById(event.taskId());

        ReviewerTaskStatusEntity reviewerTaskStatusEntity = entity.getReviewerTaskStatus();

        if (event.newStatus() == null){
            return;
        }

        switch (event.newStatus().getStatusName()) {
            case "Completed" -> {
                if (reviewerTaskStatusEntity != null && reviewerTaskStatusEntity.getTaskStatus().getStatusName().equals("Rejected")) {
                    entity.setGlobalTaskStatus(GlobalTaskStatus.REJECTED);
                } else {
                    entity.setGlobalTaskStatus(GlobalTaskStatus.COMPLETED);
                }
            }
            case "In process" -> entity.setGlobalTaskStatus(GlobalTaskStatus.IN_PROCESS);
            case "Delayed" -> entity.setGlobalTaskStatus(GlobalTaskStatus.DELAYED);
        }
    }

    @EventListener
    @Transactional
    public void updateGlobalStatusAfterChangedReviewerTaskStatus(ReviewerTaskStatusChangedEvent event){
        ProjectComponentEntity entity = projectComponentRepository.findProjectComponentEntityById(event.taskId());

        StudentTaskStatusEntity studentTaskStatusEntity = entity.getStudentTaskStatus();

        if (event.newStatus() == null){
            return;
        }

        switch (event.newStatus().getStatusName()) {
            case "Accepted" -> {
                if (studentTaskStatusEntity != null && studentTaskStatusEntity.getStatus().getStatusName().equals("Completed")) {
                    entity.setGlobalTaskStatus(GlobalTaskStatus.COMPLETED);
                }
            } case "Rejected" -> entity.setGlobalTaskStatus(GlobalTaskStatus.REJECTED);
        }
    }
}
