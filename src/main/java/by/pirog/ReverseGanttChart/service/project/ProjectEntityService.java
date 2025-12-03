package by.pirog.ReverseGanttChart.service.project;

import by.pirog.ReverseGanttChart.storage.entity.ProjectEntity;

import java.util.Optional;

public interface ProjectEntityService {

    Optional<ProjectEntity> findProjectById(Long id);
}
