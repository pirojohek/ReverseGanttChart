package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ReviewerStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewerStatusRepository extends JpaRepository<ReviewerStatusEntity, Long> {
}
