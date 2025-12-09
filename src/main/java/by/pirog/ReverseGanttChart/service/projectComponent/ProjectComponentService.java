package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreateProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.CreatedProjectComponentDto;
import by.pirog.ReverseGanttChart.dto.projectComponentDto.ProjectComponentResponseDto;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;

import java.util.List;

public interface ProjectComponentService {
    CreatedProjectComponentDto createProjectComponent(CreateProjectComponentDto createProjectComponentDto);

    ProjectComponentResponseDto getProjectComponentByIdWithoutHierarchy(Long componentId);

    List<ProjectComponentResponseDto> getProjectComponentsByProjectIdWithHierarchyOrderedByDate();


}
