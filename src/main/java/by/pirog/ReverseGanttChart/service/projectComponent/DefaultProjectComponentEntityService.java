package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.exception.ProjectComponentParentNotFound;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultProjectComponentEntityService implements ProjectComponentEntityService {

    private final ProjectComponentRepository projectComponentRepository;


    @Override
    public ProjectComponentEntity getProjectComponentByProjectIdAndComponentIdWithHierarchyAndTaskMakers
            (Long componentId) {

        var token = (CustomAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        ProjectComponentEntity parentComponent = this.projectComponentRepository.findProjectComponentEntityByProjectIdAndComponentIdWithTaskMakers(componentId, token.getProjectId())
                .orElseThrow(() -> new ProjectComponentParentNotFound("parent id not found"));

        projectComponentTree(parentComponent);

        return parentComponent;
    }

    @Override
    public ProjectComponentEntity getProjectComponentByProjectIdAndComponentIdWithTaskMakers(Long componentId) {
        var token = (CustomAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        return this.projectComponentRepository.findProjectComponentEntityByProjectIdAndComponentIdWithTaskMakers(componentId, token.getProjectId())
                .orElseThrow(() -> new ProjectComponentParentNotFound("parent id not found"));

    }

    @Override
    public void saveProjectComponent(ProjectComponentEntity projectComponentEntity) {
        this.projectComponentRepository.save(projectComponentEntity);
    }

    @Override
    public void saveProjectComponents(List<ProjectComponentEntity> projectComponentEntities) {
        this.projectComponentRepository.saveAll(projectComponentEntities);
    }

    private void projectComponentTree(ProjectComponentEntity node){
        for (ProjectComponentEntity child : node.getProjectComponentChildren()) {
            projectComponentTree(child);
        }
    }
}
