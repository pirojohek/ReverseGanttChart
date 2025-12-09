package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Long> {

    Optional<TaskStatusEntity> findTaskStatusEntityByStatusName(String status);
}
