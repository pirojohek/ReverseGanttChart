package by.pirog.ReverseGanttChart.dto.projectComponentDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class UpdatedProjectComponentResponseDto {
    private Long componentId;

    private String title;

    private String description;

    private LocalDateTime deadline;

    private LocalDateTime startDate;
}
