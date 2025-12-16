package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;

import java.util.Optional;

@FunctionalInterface
public interface GetProjectMembershipByUsernameAndProjectId {
    Optional<ProjectMembershipEntity> findProjectMembershipByUsernameAndProjectId(String email, Long projectId);
}
