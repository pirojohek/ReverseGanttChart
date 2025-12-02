package by.pirog.ReverseGanttChart.dto.projectDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ProjectInfoDto {

    private String projectName;

    private String projectDescription;

    private String projectOwnerEmail;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate deadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate createdAt;
}
