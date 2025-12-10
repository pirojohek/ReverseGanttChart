package by.pirog.ReverseGanttChart.dto.projectComponentDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UpdatedProjectComponentResponseDto {
    private Long componentId;

    private String title;

    private String description;

    private LocalDate deadline;

    private LocalDate startDate;
}
