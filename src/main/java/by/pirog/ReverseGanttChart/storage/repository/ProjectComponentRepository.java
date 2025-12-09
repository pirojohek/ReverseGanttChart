package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectComponentRepository extends JpaRepository<ProjectComponentEntity, Long> {

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.projectComponentParent " +
            "LEFT JOIN FETCH pc.projectComponentChildren children " +
            "JOIN FETCH pc.project " +
            "LEFT JOIN pc.taskMakers " +
            "LEFT JOIN children.taskMakers " +
            "WHERE pc.projectComponentParent.id = :componentId AND " +
            "pc.project.id = :projectId")
    Optional<ProjectComponentEntity> findProjectComponentEntityByProjectIdAndComponentIdWithTaskMakers
            (@Param("componentId") Long componentId, @Param("projectId") Long projectId);

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.projectComponentChildren " +
            "JOIN FETCH pc.project " +
            "WHERE pc.project.id = :projectId AND pc.id = :componentId")
    Optional<ProjectComponentEntity> findProjectComponentEntityByProjectIdAndComponentId
            (@Param("projectId") Long projectId, @Param("componentId") Long componentId);

    @EntityGraph(attributePaths = {
            "project",
            "projectComponentParent",
            "creator",
            "creator.user",
            "creator.userRole",
            "studentTaskStatus",
            "reviewerTaskStatus"
    })
    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "WHERE pc.project.id = :projectId AND pc.id = :componentId")
    Optional<ProjectComponentEntity>
            findProjectComponentEntityByProjectIdAndComponentIdWithAllProperties(@Param("projectId") Long projectId, @Param("componentId") Long componentId);


    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.projectComponentChildren " +
            "LEFT JOIN FETCH pc.project " +
            "LEFT JOIN FETCH pc.projectComponentParent " +
            "LEFT JOIN FETCH pc.creator " +
            "WHERE pc.project.id = :projectId AND pc.id = :componentId")
    Optional<ProjectComponentEntity> findProjectComponentEntityByProjectIdAndComponentIdWithChildren
            (@Param("projectId") Long projectId, @Param("componentId") Long componentId);


}
