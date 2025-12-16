package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectInviteRepository extends JpaRepository<ProjectInviteEntity, Long> {

    @Query("SELECT pi FROM ProjectInviteEntity pi " +
            "JOIN FETCH pi.project " +
            "JOIN FETCH pi.user " +
            "WHERE pi.user.email = :email and pi.project.id = :projectId")
    Optional<ProjectInviteEntity> findByEmailAndProjectId(String email, Long projectId);


    Optional<ProjectInviteEntity> findProjectInviteEntityByToken(String token);

    @Query("SELECT pi FROM ProjectInviteEntity pi " +
            "JOIN FETCH pi.project " +
            "JOIN FETCH pi.userRole " +
            "JOIN FETCH pi.user " +
            "WHERE pi.project.id = :projectId")
    List<ProjectInviteEntity> findAllByProjectId(@Param("projectId") Long projectId);
}
