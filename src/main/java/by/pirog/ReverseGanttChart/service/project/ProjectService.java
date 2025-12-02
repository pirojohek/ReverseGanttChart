package by.pirog.ReverseGanttChart.service.project;

import by.pirog.ReverseGanttChart.dto.projectDto.*;

public interface ProjectService {

    CreatedProjectDto createProject(CreateProjectDto projectDto);

    void deleteProject();

    UpdatedProjectDto updateProject(UpdateProjectDto dto);

    ProjectInfoDto getProjectInfo();
}
