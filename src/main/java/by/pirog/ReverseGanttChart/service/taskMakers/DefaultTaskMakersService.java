package by.pirog.ReverseGanttChart.service.taskMakers;

import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;
import by.pirog.ReverseGanttChart.service.projectComponent.ProjectComponentEntityService;
import by.pirog.ReverseGanttChart.service.projectComponent.ProjectComponentService;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.service.user.UserService;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.entity.TaskMakerEntity;
import by.pirog.ReverseGanttChart.storage.repository.TaskMakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultTaskMakersService implements TaskMakersService {

    private final TaskMakerRepository taskMakerRepository;

    private final MembershipService membershipService;

    private final ProjectComponentEntityService projectComponentEntityService;

    @Override
    public List<TakenTaskResponseDto> takeTasksToMake(Long taskId, Boolean subtasks) {

        ProjectMembershipEntity membership = this.membershipService.getCurrentProjectMembership();

        ProjectComponentEntity projectComponent = null;
        if (subtasks) {
            projectComponent = this.projectComponentEntityService
                    .getProjectComponentByProjectIdAndComponentIdWithTaskMakers(taskId);
            setTaskMaker(true, membership, projectComponent);
        } else {
            projectComponent = this.projectComponentEntityService
                    .getProjectComponentByProjectIdAndComponentIdWithTaskMakers(taskId);
            setTaskMaker(false, membership, projectComponent);
        }

        this.projectComponentEntityService.saveProjectComponent(projectComponent);


        // теперь нужно все переделать каким то образом
        return null;
    }

    private void assignToComponent(ProjectMembershipEntity membership, ProjectComponentEntity projectComponent) {
        TaskMakerEntity taskMaker = TaskMakerEntity.builder()
                .membership(membership)
                .projectComponent(projectComponent)
                .build();
        projectComponent.addTaskMaker(taskMaker);
    }

    private void setTaskMaker(Boolean recursive, ProjectMembershipEntity membership,
                              ProjectComponentEntity projectComponent) {

        assignToComponent(membership, projectComponent);
        if (recursive) {
            for (ProjectComponentEntity child : projectComponent.getProjectComponentChildren()) {
                setTaskMaker(true, membership, child);
            }
        }
    }
}
