package by.pirog.ReverseGanttChart.dto.projectDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public record UpdateProjectDto(
        String name,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate deadline
){

}
