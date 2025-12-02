package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ReviewerTaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewerTaskStatusRepository extends JpaRepository<ReviewerTaskStatusEntity, Long> {
}
