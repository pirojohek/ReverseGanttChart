package by.pirog.ReverseGanttChart.mapper;

import by.pirog.ReverseGanttChart.dto.taskMakerDto.TakenTaskResponseDto;
import by.pirog.ReverseGanttChart.dto.taskMakerDto.TaskMakerResponseDto;
import by.pirog.ReverseGanttChart.storage.entity.TaskMakerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {ProjectMembershipMapper.class}
)
public interface TaskMakerMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "taskId", source = "projectComponent.id")
    @Mapping(target = "taskMakerInfo", source = "membership", qualifiedByName = "ToInfoMembershipDtoOrNull")
    TaskMakerResponseDto toDto(TaskMakerEntity task);

    @Mapping(target = "taskId", source = "projectComponent.id")
    @Mapping(target = "taskMaker", source = "membership", qualifiedByName = "ToInfoMembershipDtoOrNull")
    TakenTaskResponseDto toTakenTaskResponseDto(TaskMakerEntity task);

    @Named("TaskMakerSetToList")
    default List<TaskMakerResponseDto> taskMakerSetToList(Set<TaskMakerEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
