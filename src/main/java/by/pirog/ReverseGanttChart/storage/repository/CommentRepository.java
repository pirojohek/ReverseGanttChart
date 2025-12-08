package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {


}
