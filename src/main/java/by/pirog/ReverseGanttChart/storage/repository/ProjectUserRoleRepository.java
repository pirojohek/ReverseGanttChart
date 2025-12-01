package by.pirog.ReverseGanttChart.storage.repository;

import by.pirog.ReverseGanttChart.storage.entity.ProjectUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectUserRoleRepository extends JpaRepository<ProjectUserRoleEntity, Long> {

    Optional<ProjectUserRoleEntity> findProjectUserRoleEntityByRoleName(String roleName);
}
