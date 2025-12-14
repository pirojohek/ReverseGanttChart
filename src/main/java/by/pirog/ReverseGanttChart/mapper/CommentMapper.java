package by.pirog.ReverseGanttChart.mapper;


import by.pirog.ReverseGanttChart.dto.commentDto.CommentResponseDto;
import by.pirog.ReverseGanttChart.dto.commentDto.CreatedCommentDto;
import by.pirog.ReverseGanttChart.mapper.qualifier.DateTimeQualifier;
import by.pirog.ReverseGanttChart.storage.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses =
                {ProjectMembershipMapper.class,
                        DateTimeQualifier.class})
public interface CommentMapper {


    @Mapping(target = "id", source = "commentId")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "commenter", source = "commenter", qualifiedByName = "ToInfoMembershipDtoOrNull")
    @Mapping(target = "taskId", source = "projectComponent.id")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "InstantToLocalDateTime")
    CommentResponseDto toDto(CommentEntity entity);


    @Mapping(target = "id", source = "commentId")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "commenter", source = "commenter", qualifiedByName = "ToInfoMembershipDtoOrNull")
    @Mapping(target = "taskId", source = "projectComponent.id")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "InstantToLocalDateTime")
    CreatedCommentDto toCreatedCommentDto(CommentEntity entity);

    @Named("CommentSetToList")
    default List<CommentResponseDto> toDtoList(Set<CommentEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        return entities.stream()
                .filter(entity -> entity.getCreatedAt() != null)
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
