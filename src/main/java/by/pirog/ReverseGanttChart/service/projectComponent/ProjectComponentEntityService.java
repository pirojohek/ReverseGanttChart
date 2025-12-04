package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;

import java.util.List;

public interface ProjectComponentEntityService {
    ProjectComponentEntity getProjectComponentByProjectIdAndComponentIdWithHierarchyAndTaskMakers
            (Long componentId);

    ProjectComponentEntity getProjectComponentByProjectIdAndComponentIdWithTaskMakers(Long componentId);

    void saveProjectComponent(ProjectComponentEntity projectComponentEntity);

    void saveProjectComponents(List<ProjectComponentEntity> projectComponentEntities);
}
