package by.pirog.ReverseGanttChart.dto.projectDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record CreateProjectDto(

        String projectName,

        String description

) {

}