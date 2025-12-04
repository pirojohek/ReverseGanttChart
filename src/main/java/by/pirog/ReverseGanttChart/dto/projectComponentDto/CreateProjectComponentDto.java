package by.pirog.ReverseGanttChart.dto.projectComponentDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateProjectComponentDto(

        @NotNull
        String title,

        String description,


        Long parentId,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate deadline
) {
}
