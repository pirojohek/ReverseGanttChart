package by.pirog.ReverseGanttChart.service.taskMakers;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;
import by.pirog.ReverseGanttChart.mapper.ProjectMembershipMapper;
import by.pirog.ReverseGanttChart.mapper.TaskMakerMapper;
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

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultTaskMakersService implements TaskMakersService {

    private final TaskMakerRepository taskMakerRepository;

    private final MembershipService membershipService;

    private final ProjectComponentEntityService projectComponentEntityService;

    private final ProjectMembershipMapper projectMembershipMapper;

    @Override
    public List<TakenTaskResponseDto> takeTasksToMake(Long taskId, Boolean subtasks) {
        ProjectMembershipEntity membership = this.membershipService.getCurrentProjectMembership();

        return tasksToMake(membership, taskId, subtasks);
    }

    @Override
    public List<TakenTaskResponseDto> giveMembershipTasksToMake(String email, Long taskId, Boolean subtasks) {
        ProjectMembershipEntity membership = this.membershipService.getProjectMembershipByEmail(email);

        return tasksToMake(membership, taskId, subtasks);
    }

    @Override
    public List<TakenTaskResponseDto> tasksToMake(ProjectMembershipEntity membership, Long taskId, Boolean subtasks) {

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

        List<TakenTaskResponseDto> result = new ArrayList<>();

        collectAssignedTasks(projectComponent, membership, result);

        return result;

    }


    @Override
    public void removeTaskMakerById(Long taskMakerId) {
        this.taskMakerRepository.deleteById(taskMakerId);
    }


    private void collectAssignedTasks(ProjectComponentEntity node,
                                      ProjectMembershipEntity membership,
                                      List<TakenTaskResponseDto> result) {
        if (node == null) {
            return;
        }
        result.add(convertToDto(node, membership));

        for (ProjectComponentEntity child : node.getProjectComponentChildren()) {
            collectAssignedTasks(child, membership, result);
        }
    }

    private TakenTaskResponseDto convertToDto(ProjectComponentEntity projectComponentEntity,
                                                                ProjectMembershipEntity projectMembershipEntity) {
        return TakenTaskResponseDto.builder()
                .taskMaker(this.projectMembershipMapper.toInfoProjectMembershipDto(projectMembershipEntity))
                .taskId(projectComponentEntity.getId())
                .build();
    }

    private void assignToComponent(ProjectMembershipEntity membership, ProjectComponentEntity projectComponent) {

        Optional<TaskMakerEntity> maker = this.taskMakerRepository.findTaskMakerEntityByMembershipIdAndProjectComponentId(membership.getId(), projectComponent.getId());

        if (maker.isEmpty()){
            TaskMakerEntity taskMaker = TaskMakerEntity.builder()
                    .membership(membership)
                    .projectComponent(projectComponent)
                    .build();
            projectComponent.addTaskMaker(taskMaker);
        }

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
