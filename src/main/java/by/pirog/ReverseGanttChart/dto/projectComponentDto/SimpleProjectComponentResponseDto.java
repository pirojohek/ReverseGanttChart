package by.pirog.ReverseGanttChart.dto.projectComponentDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SimpleProjectComponentResponseDto {
    private String title;
    private String description;
    private LocalDateTime createdDate;
}
// Todo ну это для статистики какой нибудь дтошка, без всякой лабуды