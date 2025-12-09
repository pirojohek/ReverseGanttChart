package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.TaskMakerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskMakerRepository extends JpaRepository<TaskMakerEntity, Long> {

    @Query("SELECT tm FROM TaskMakerEntity tm " +
            "JOIN FETCH tm.membership " +
            "JOIN FETCH tm.projectComponent " +
            "WHERE tm.membership.id = :membershipId and tm.projectComponent.id = :componentId")
    Optional<TaskMakerEntity> findTaskMakerEntityByMembershipIdAndProjectComponentId
            (@Param("membershipId") Long membershipId, @Param("componentId") Long componentId);
}
