package by.pirog.ReverseGanttChart.dto.projectComponentDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateProjectComponentDto(

        @NotNull
        String title,

        String description,


        Long parentId,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate deadlineDate,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime deadlineTime,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime startTime
) {
}
