package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.StudentTaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentTaskStatusRepository extends JpaRepository<StudentTaskStatusEntity, Long> {
}
