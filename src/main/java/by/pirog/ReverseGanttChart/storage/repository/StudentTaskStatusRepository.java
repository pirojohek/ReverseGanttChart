package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.StudentTaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentTaskStatusRepository extends JpaRepository<StudentTaskStatusEntity, Long> {

    @Query("SELECT sts FROM StudentTaskStatusEntity sts " +
            "JOIN FETCH sts.task " +
            "WHERE sts.task.id = :taskId")
    Optional<StudentTaskStatusEntity> findStudentTaskStatusEntityByProjectComponentId(@Param("taskId") Long taskId);

}
