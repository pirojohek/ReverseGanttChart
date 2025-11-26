package by.pirog.ReverseGanttChart.service.project;

import by.pirog.ReverseGanttChart.dto.projectDto.CreateProjectDto;
import by.pirog.ReverseGanttChart.dto.projectDto.CreatedProjectDto;

public interface ProjectService {

    CreatedProjectDto createProject(CreateProjectDto projectDto);
}
