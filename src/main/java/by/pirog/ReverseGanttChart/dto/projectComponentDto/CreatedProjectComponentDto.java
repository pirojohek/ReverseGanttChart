package by.pirog.ReverseGanttChart.dto.projectComponentDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreatedProjectComponentDto {
    @NotNull
    private String title;

    private String description;

    private Long parentId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private String creator;

    private String role;
}
