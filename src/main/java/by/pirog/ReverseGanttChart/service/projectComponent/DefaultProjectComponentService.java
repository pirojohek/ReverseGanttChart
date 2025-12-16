package by.pirog.ReverseGanttChart.service.projectComponent;

import by.pirog.ReverseGanttChart.dto.projectComponentDto.*;
import by.pirog.ReverseGanttChart.exception.ProjectComponentNotFoundException;
import by.pirog.ReverseGanttChart.exception.ProjectComponentParentNotFound;
import by.pirog.ReverseGanttChart.exception.ProjectNotFoundException;
import by.pirog.ReverseGanttChart.exception.UserIsNotMemberInProjectException;
import by.pirog.ReverseGanttChart.mapper.ProjectComponentMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.service.project.ProjectEntityService;
import by.pirog.ReverseGanttChart.service.projectMembership.GetProjectMembershipByUserEmailAndProjectId;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
/*
 Todo - что касается логики изменения статуса задачи, по идее если предыдущие этапы не выполнены
 Todo - то невозможно будет поставить статус выполнено у родительской задачи
 Todo - надо еще короче сделать статус, что задача просрочена или пора начинать работать, короче это временной статус или можно сказать итоговый
 */
public class DefaultProjectComponentService implements ProjectComponentService {

    private final ProjectEntityService projectService;

    private final ProjectComponentRepository projectComponentRepository;

    private final GetProjectMembershipByUserEmailAndProjectId getProjectMembershipByUserEmailAndProjectId;


    private final ProjectComponentMapper projectComponentMapper;

    @Override
    public CreatedProjectComponentDto createProjectComponent(CreateProjectComponentDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var token = (CustomAuthenticationToken) authentication;

        var customUserDetails = (CustomUserDetails) authentication.getDetails();

        ProjectMembershipEntity creator =
                getProjectMembershipByUserEmailAndProjectId.findProjectMembershipByUserEmailAndProjectId(customUserDetails.getEmail(), token.getProjectId())
                        .orElseThrow(() -> new UserIsNotMemberInProjectException(customUserDetails.getEmail()));

        ProjectComponentEntity parent = null;
        if (dto.parentId() != null) {
            parent = projectComponentRepository.
                    findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(),
                            dto.parentId())
                    .orElseThrow(() -> new ProjectComponentParentNotFound("parent id not found"));
        }

        ProjectEntity project = this.projectService.findProjectById(token.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(token.getProjectId().toString()));

        ProjectComponentEntity entity = this.projectComponentMapper.toEntity(dto, creator, project, parent);

        if (entity.getStartDate().isAfter(entity.getDeadline())){
            throw new ValidationException("start date is after deadline");
        }
        if (entity.getStartDate().isBefore(Instant.now())){
            throw new ValidationException("start date is before now");
        }

        entity = this.projectComponentRepository.save(entity);

        return this.projectComponentMapper.toCreatedProjectComponentDto(entity);
    }


    @Override
    public ProjectComponentResponseDto getProjectComponentByIdWithoutHierarchy(Long componentId) {

        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectComponentEntity entity = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentIdWithAllProperties(token.getProjectId(), componentId)
                .orElseThrow(() -> new ProjectComponentNotFoundException(componentId.toString()));

        return this.projectComponentMapper.toProjectComponentResponseDto(entity);
    }

    @Override
    public List<ProjectComponentResponseDto> getProjectComponentsByProjectIdWithHierarchyOrderedByDate() {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        List<ProjectComponentEntity> allComponents = projectComponentRepository.findAllByProjectIdWithGraph(token.getProjectId());

        return this.projectComponentMapper.toHierarchyDtoList(allComponents);
    }

    // Todo - хочу на mapper это переписать
    @Override
    public UpdatedProjectComponentResponseDto patchProjectComponent(UpdateProjectComponentRequestDto dto) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        ProjectComponentEntity entity = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentIdWithAllProperties(token.getProjectId(), dto.componentId())
                .orElseThrow(() -> new ProjectComponentNotFoundException(dto.componentId().toString()));

        this.projectComponentMapper.updateEntityFromDto(dto, entity);
        this.projectComponentRepository.save(entity);

        return this.projectComponentMapper.toUpdatedProjectComponentDto(entity);

    }

    @Override
    public void deleteProjectComponentById(Long componentId) {
        var token = (CustomAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        ProjectComponentEntity component = projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(), componentId)
                .orElseThrow(() -> new ProjectComponentNotFoundException(componentId.toString()));

        if (component.getProjectComponentParent() != null) {
            ProjectComponentEntity parent = component.getProjectComponentParent();
            parent.getProjectComponentChildren().remove(component);
            component.setProjectComponentParent(null);
            projectComponentRepository.save(parent);
        }

        this.projectComponentRepository.delete(component);
    }

}
