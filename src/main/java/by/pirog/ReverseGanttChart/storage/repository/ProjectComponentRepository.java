package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectComponentRepository extends JpaRepository<ProjectComponentEntity, Long> {
}
