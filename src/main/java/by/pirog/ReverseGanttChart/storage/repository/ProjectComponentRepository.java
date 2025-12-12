package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectComponentRepository extends JpaRepository<ProjectComponentEntity, Long> {

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.projectComponentParent " +
            "LEFT JOIN FETCH pc.projectComponentChildren children " +
            "JOIN FETCH pc.project " +
            "LEFT JOIN pc.taskMakers " +
            "LEFT JOIN children.taskMakers " +
            "WHERE pc.id = :componentId AND " +
            "pc.project.id = :projectId")
    Optional<ProjectComponentEntity> findProjectComponentEntityByProjectIdAndComponentIdWithTaskMakers
            (@Param("componentId") Long componentId, @Param("projectId") Long projectId);

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.projectComponentChildren " +
            "JOIN FETCH pc.project " +
            "WHERE pc.project.id = :projectId AND pc.id = :componentId")
    Optional<ProjectComponentEntity> findProjectComponentEntityByProjectIdAndComponentId
            (@Param("projectId") Long projectId, @Param("componentId") Long componentId);

    @Query("""
        SELECT DISTINCT pc FROM ProjectComponentEntity pc
        LEFT JOIN pc.projectComponentParent parent
        LEFT JOIN pc.creator cr
        LEFT JOIN cr.user crUser
        LEFT JOIN cr.userRole crRole
        LEFT JOIN pc.project proj
        LEFT JOIN pc.studentTaskStatus studentStatus
        LEFT JOIN pc.reviewerTaskStatus reviewerStatus
        WHERE pc.project.id = :projectId AND pc.id = :componentId
    """)
    Optional<ProjectComponentEntity>
    findProjectComponentEntityByProjectIdAndComponentIdWithAllProperties(@Param("projectId") Long projectId,
                                                                         @Param("componentId") Long componentId);

    @EntityGraph(attributePaths = {
            "creator",
            "creator.user",
            "creator.userRole",
            "studentTaskStatus",
            "studentTaskStatus.status",
            "reviewerTaskStatus",
            "reviewerTaskStatus.taskStatus"
    })
    @Query("SELECT pc FROM ProjectComponentEntity pc WHERE pc.project.id = :projectId")
    List<ProjectComponentEntity> findAllByProjectIdWithGraph(@Param("projectId") Long projectId);


    // Поиск корневых элементов (без родителя) для проекта
    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "WHERE pc.project.id = :projectId AND pc.projectComponentParent IS NULL " +
            "ORDER BY pc.createdAt ASC")
    List<ProjectComponentEntity> findRootComponentsByProjectId(
            @Param("projectId") Long projectId
    );


    // Найти всех детей для родителя
    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "WHERE pc.projectComponentParent.id = :parentId " +
            "ORDER BY pc.pos ASC")
    List<ProjectComponentEntity> findChildrenByParentId(@Param("parentId") Long parentId);

}
