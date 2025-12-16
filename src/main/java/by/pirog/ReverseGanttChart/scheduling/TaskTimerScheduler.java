package by.pirog.ReverseGanttChart.scheduling;

import by.pirog.ReverseGanttChart.enums.GlobalTaskStatus;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskTimerScheduler {

    private final ProjectComponentRepository projectComponentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void updateStatuses(){
        Instant now = Instant.now();
        updatePlannedToInProcess(now);
        updateDeadlineIsOverToOverdue(now);
    }

    private void updateDeadlineIsOverToOverdue(Instant now) {
        List<ProjectComponentEntity> tasks = this.projectComponentRepository.findProjectComponentWhereDeadlineIsOver(now);
        tasks.forEach(task -> {
             task.setGlobalTaskStatus(GlobalTaskStatus.OVERDUE);
        });
    }

    private void updatePlannedToInProcess(Instant now) {
        List<ProjectComponentEntity> tasks = this.projectComponentRepository.findProjectComponentWhereTimeToDo(now);
        tasks.forEach(task -> {
            task.setGlobalTaskStatus(GlobalTaskStatus.IT_IS_TIME);
        });
    }
}
