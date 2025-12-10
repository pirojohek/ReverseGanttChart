package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.dto.projectComponentDto.*;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;

import java.util.List;

public interface ProjectComponentService {
    CreatedProjectComponentDto createProjectComponent(CreateProjectComponentDto createProjectComponentDto);

    ProjectComponentResponseDto getProjectComponentByIdWithoutHierarchy(Long componentId);

    List<ProjectComponentResponseDto> getProjectComponentsByProjectIdWithHierarchyOrderedByDate();

    UpdatedProjectComponentResponseDto patchProjectComponent(UpdateProjectComponentRequestDto dto);

    void deleteProjectComponentById(Long componentId);
}
