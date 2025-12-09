package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ReviewerTaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewerTaskStatusRepository extends JpaRepository<ReviewerTaskStatusEntity, Long> {

    @Query("SELECT rst FROM ReviewerTaskStatusEntity rst " +
            "JOIN FETCH rst.task " +
            "WHERE rst.task.id = :taskId")
    Optional<ReviewerTaskStatusEntity> findReviewerTaskStatusEntitiesByProjectComponentId(@Param("taskId") Long taskId);
}
