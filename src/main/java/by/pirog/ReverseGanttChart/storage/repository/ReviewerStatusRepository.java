package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ReviewerStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewerStatusRepository extends JpaRepository<ReviewerStatusEntity, Long> {

    Optional<ReviewerStatusEntity> findReviewerStatusEntityByStatusName(String name);
}
