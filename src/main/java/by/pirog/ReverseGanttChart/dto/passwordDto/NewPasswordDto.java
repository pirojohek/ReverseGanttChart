package by.pirog.ReverseGanttChart.dto.passwordDto;

import jakarta.validation.constraints.NotNull;

public record NewPasswordDto(
        @NotNull
        String password,

        String token
) {

}
