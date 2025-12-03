package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectComponentRepository extends JpaRepository<ProjectComponentEntity, Long> {

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "JOIN FETCH pc.projectComponentParent " +
            "JOIN FETCH pc.projectComponentChildren " +
            "JOIN FETCH pc.project " +
            "WHERE pc.projectComponentParent.id = :componentId AND " +
            "pc.project.id = :projectId")
    Optional<ProjectComponentEntity> findProjectComponentEntityByParentIdAndProjectId
            (@Param("componentId") Long componentId, @Param("projectId") Long projectId);
}
