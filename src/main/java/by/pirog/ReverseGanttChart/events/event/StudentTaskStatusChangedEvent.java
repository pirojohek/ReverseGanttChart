package by.pirog.ReverseGanttChart.events.event;

import by.pirog.ReverseGanttChart.storage.entity.TaskStatusEntity;

public record StudentTaskStatusChangedEvent(
        Long taskId,
        TaskStatusEntity newStatus
) {
}
