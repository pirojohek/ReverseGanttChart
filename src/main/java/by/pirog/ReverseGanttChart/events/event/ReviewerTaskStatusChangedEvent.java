package by.pirog.ReverseGanttChart.events.event;

import by.pirog.ReverseGanttChart.storage.entity.ReviewerStatusEntity;
import by.pirog.ReverseGanttChart.storage.entity.ReviewerTaskStatusEntity;

public record ReviewerTaskStatusChangedEvent(
        Long taskId,
        ReviewerStatusEntity newStatus
) {
}
