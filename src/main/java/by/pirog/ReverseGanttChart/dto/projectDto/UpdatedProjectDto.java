package by.pirog.ReverseGanttChart.dto.projectDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Data
public class UpdatedProjectDto {
    private String projectName;
    private String projectDescription;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate deadline;
}
