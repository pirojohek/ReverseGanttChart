package by.pirog.ReverseGanttChart.mapper;

import by.pirog.ReverseGanttChart.dto.studentStatusDto.StudentTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.storage.entity.StudentTaskStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
        uses = {
                ProjectMembershipMapper.class,
        })
public interface StudentTaskStatusMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "student", source = "student", qualifiedByName = "ToInfoMembershipDtoOrNull")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "status", source = "status.statusName")
    StudentTaskStatusResponseDto toDto(StudentTaskStatusEntity entity);

    @Named("ToStudentTaskStatusDtoOrNull")
    default StudentTaskStatusResponseDto toStudentTaskStatusDto(StudentTaskStatusEntity entity) {
        return entity != null ? toDto(entity) : null;
    }


}
