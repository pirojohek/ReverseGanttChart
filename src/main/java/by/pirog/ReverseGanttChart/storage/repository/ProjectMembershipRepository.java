package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembershipEntity, Long> {

    @Query("SELECT pm FROM ProjectMembershipEntity pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "JOIN FETCH pm.userRole " +
            "WHERE pm.user.email = :email AND pm.project.id = :projectId")
    Optional<ProjectMembershipEntity> findByUserEmailAndProjectId(@Param("email") String email, @Param("projectId") Long projectId);

    List<ProjectMembershipEntity> findAllByProjectId(Long projectId);
}
