package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.TaskMakerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskMakerRepository extends JpaRepository<TaskMakerEntity, Long> {
}
