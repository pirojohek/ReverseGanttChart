package by.pirog.ReverseGanttChart.service.studentTaskStatus;

import by.pirog.ReverseGanttChart.dto.studentStatusDto.SetTaskStatusRequestDto;
import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.exception.ProjectComponentNotFoundException;
import by.pirog.ReverseGanttChart.exception.TaskStatusNotFoundException;
import by.pirog.ReverseGanttChart.exception.UserIsNotTaskMakerException;
import by.pirog.ReverseGanttChart.mapper.StudentTaskStatusMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import by.pirog.ReverseGanttChart.storage.entity.*;
import by.pirog.ReverseGanttChart.storage.repository.ProjectComponentRepository;
import by.pirog.ReverseGanttChart.storage.repository.StudentTaskStatusRepository;
import by.pirog.ReverseGanttChart.storage.repository.TaskMakerRepository;
import by.pirog.ReverseGanttChart.storage.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultStudentTaskStatusService implements StudentTaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final StudentTaskStatusRepository studentTaskStatusRepository;

    private final TaskMakerRepository taskMakerRepository;
    private final ProjectComponentRepository projectComponentRepository;

    private final MembershipService membershipService;

    private final StudentTaskStatusMapper studentTaskStatusMapper;
    /*
     тут видимо прикольная логика будет, если чувак поставит статус невыполнено на дочерней задаче
     то нужно сделать так, чтобы у родительской задачи тоже стал статус невыполнено
     */
    @Override
    public StudentTaskStatusResponseDto setTaskStatus(SetTaskStatusRequestDto dto) {
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ProjectMembershipEntity membership = this.membershipService.getCurrentProjectMembership();

        ProjectComponentEntity projectComponent = this.projectComponentRepository
                .findProjectComponentEntityByProjectIdAndComponentId(token.getProjectId(), dto.taskId())
                .orElseThrow(() -> new ProjectComponentNotFoundException("Task not found"));


        TaskMakerEntity taskMaker = this.taskMakerRepository
                .findTaskMakerEntityByMembershipIdAndProjectComponentId(membership.getId(), projectComponent.getId())
                .orElseThrow(() -> new UserIsNotTaskMakerException("user is not task maker "));


        TaskStatusEntity taskStatusEntity = this.taskStatusRepository
                .findTaskStatusEntityByStatusName(dto.status())
                .orElseThrow(() -> new TaskStatusNotFoundException("Status not found"));

        Optional<StudentTaskStatusEntity> existingStatusOpt = this.studentTaskStatusRepository
                .findStudentTaskStatusEntityByProjectComponentId(projectComponent.getId());

        StudentTaskStatusEntity studentTaskStatusEntity;

        if (existingStatusOpt.isPresent()) {
            studentTaskStatusEntity = existingStatusOpt.get();
            studentTaskStatusEntity.setStatus(taskStatusEntity);
            studentTaskStatusEntity.setStudent(membership);
        } else {

            studentTaskStatusEntity = StudentTaskStatusEntity.builder()
                    .student(membership)
                    .task(projectComponent)
                    .status(taskStatusEntity)
                    .build();
        }

        studentTaskStatusEntity = studentTaskStatusRepository.save(studentTaskStatusEntity);

        return this.studentTaskStatusMapper.toStudentTaskStatusDto(studentTaskStatusEntity);
    }
}
