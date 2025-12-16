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
            "WHERE pm.user.username = :username AND pm.project.id = :projectId")
    Optional<ProjectMembershipEntity> findByUsernameAndProjectId(@Param("username") String username, @Param("projectId") Long projectId);

    List<ProjectMembershipEntity> findAllByProjectId(Long projectId);

    @Query("SELECT pm FROM ProjectMembershipEntity pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.userRole " +
            "JOIN FETCH pm.project " +
            "WHERE pm.projectUsername = :projectUsername AND pm.project.id = :projectId")
    Optional<ProjectMembershipEntity> findProjectMembershipByProjectUsernameAndProjectId
            (@Param("projectUsername") String projectUsername, @Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMembershipEntity pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "JOIN FETCH pm.userRole " +
            "where pm.user.email = :email")
    List<ProjectMembershipEntity> findAllByUserEmail(@Param("email") String email);

    @Query("SELECT pm from ProjectMembershipEntity pm " +
            "JOIN FETCH pm.project " +
            "WHERE pm.projectUsername = :username and pm.project.id = :projectId")
    Optional<ProjectMembershipEntity> findProjectMembershipByUsernameAndProjectId(@Param("username") String username, @Param("projectId") Long projectId);


    @Query("SELECT pm FROM ProjectMembershipEntity pm " +
            "JOIN FETCH pm.user " +
            "JOIN FETCH pm.project " +
            "JOIN FETCH pm.userRole " +
            "WHERE pm.user.email = :email AND pm.project.id = :projectId")
    Optional<ProjectMembershipEntity> findProjectMembershipByUserEmailAndProjectId(@Param("email") String email, @Param("projectId") Long projectId);
}
