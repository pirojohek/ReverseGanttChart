package by.pirog.ReverseGanttChart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    // Todo - здесь добавить username
    private Long id;
    private String email;
}
