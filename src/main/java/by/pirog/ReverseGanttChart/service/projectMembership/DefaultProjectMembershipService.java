package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProjectMembershipService implements GetProjectMembershipByUserEmailAndProjectId {

    private final ProjectMembershipRepository projectMembershipRepository;
    // сначала видимо надо получить ProjectEntity, а уже потом нужно получать
    @Override
    public Optional<ProjectMembershipEntity> findProjectMembershipByUserEmailAndProjectId(String email, Long projectId) {
        return projectMembershipRepository.findByUserEmailAndProjectId(email, projectId);
    }

}
