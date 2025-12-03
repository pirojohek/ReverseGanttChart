package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findProjectById(Long id);
}
