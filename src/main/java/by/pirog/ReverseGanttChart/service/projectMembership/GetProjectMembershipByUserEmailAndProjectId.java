package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;

import java.util.Optional;

@FunctionalInterface
public interface GetProjectMembershipByUserEmailAndProjectId {
    Optional<ProjectMembershipEntity> findProjectMembershipByUserEmailAndProjectId(String email, Long projectId);
}
