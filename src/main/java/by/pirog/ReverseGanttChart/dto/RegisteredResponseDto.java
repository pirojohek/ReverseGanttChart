package by.pirog.ReverseGanttChart.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisteredResponseDto {
    private String email;
    private String username;
}
