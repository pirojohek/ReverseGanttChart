package by.pirog.ReverseGanttChart.mapper;

import by.pirog.ReverseGanttChart.dto.invite.InviteResponseDto;
import by.pirog.ReverseGanttChart.mapper.qualifier.DateTimeQualifier;
import by.pirog.ReverseGanttChart.storage.entity.ProjectInviteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
                DateTimeQualifier.class,
        })
public interface InviteMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "inviteStatus", source = "inviteStatus")
    @Mapping(target = "userRole", source = "userRole.roleName")
    @Mapping(target = "invitedPersonEmail", source = "user.email")
    @Mapping(target = "inviter", source = "inviter.projectUsername")
    @Mapping(target = "inviteDate", source = "createdAt", qualifiedByName = "InstantToLocalDateTime")
    InviteResponseDto projectInviteEntityToDto(ProjectInviteEntity projectInviteEntity);

    // Todo - сделать сортировку по времени
    default List<InviteResponseDto> listEntitiesToListResponseDto(List<ProjectInviteEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream().map(this::projectInviteEntityToDto)
                .toList();
    }
}
