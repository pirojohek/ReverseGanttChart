package by.pirog.ReverseGanttChart.dto.projectComponentDto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record UpdateProjectComponentRequestDto(
        Long componentId,

        String title,

        String description,

        LocalDate deadline,

        LocalDate startDate
) {
    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasDeadline() {
        return deadline != null;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }
}
