package by.pirog.ReverseGanttChart.dto.projectDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public record UpdateProjectDto(
        String projectName,
        String projectDescription,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate deadline
){
        public boolean hasProjectName() {
                return projectName != null;
        }

        public boolean hasDescription() {
                return projectDescription != null;
        }

        public boolean hasDeadline() {
                return deadline != null;
        }
}
