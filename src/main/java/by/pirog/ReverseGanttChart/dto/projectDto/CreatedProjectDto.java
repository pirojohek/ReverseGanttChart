package by.pirog.ReverseGanttChart.dto.projectDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  CreatedProjectDto {

    private String projectName;
    private String projectDescription;

    private String projectOwnerEmail;
}
