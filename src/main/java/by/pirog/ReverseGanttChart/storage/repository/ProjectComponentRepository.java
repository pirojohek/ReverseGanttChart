package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProjectComponentRepository extends JpaRepository<ProjectComponentEntity, Long> {

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.projectComponentParent " +
            "LEFT JOIN FETCH pc.projectComponentChildren children " +
            "JOIN FETCH pc.project " +
            "LEFT JOIN FETCH pc.taskMakers " +
            "LEFT JOIN FETCH children.taskMakers " +
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


    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "LEFT JOIN FETCH pc.studentTaskStatus " +
            "LEFT JOIN FETCH pc.reviewerTaskStatus ")
    ProjectComponentEntity findProjectComponentEntityById(Long id);


    // ============ ЗАПРОСЫ ДЛЯ SCHEDULER ===============
    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "WHERE pc.startDate <= :now " +
            "AND pc.globalTaskStatus = 'PLANNED' ")
    List<ProjectComponentEntity> findProjectComponentWhereTimeToDo(Instant now);

    @Query("SELECT pc FROM ProjectComponentEntity pc " +
            "WHERE pc.deadline <= :now " +
            "AND pc.globalTaskStatus IN ('DELAYED', 'IT_IS_TIME', 'IN_PROGRESS',\n" +
            "    'REJECTED')")
    List<ProjectComponentEntity> findProjectComponentWhereDeadlineIsOver(Instant now);
}
