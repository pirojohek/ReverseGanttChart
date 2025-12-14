package by.pirog.ReverseGanttChart.dto.projectComponentDto;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record UpdateProjectComponentRequestDto(
        Long componentId,

        String title,

        String description,

        LocalDate deadlineDate,

        LocalTime deadlineTime,

        LocalDate startDate,
        LocalTime startTime
) {
    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasDeadlineDate() {
        return deadlineDate != null;
    }

    public boolean hasDeadlineTime() {
        return deadlineTime != null;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }
    public boolean hasStartTime() {
        return startTime != null;
    }
}
