package by.pirog.ReverseGanttChart.mapper;

import by.pirog.ReverseGanttChart.dto.reviwerStatusDto.ReviewerTaskStatusResponseDto;
import by.pirog.ReverseGanttChart.storage.entity.ReviewerTaskStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
        uses = {
                ProjectMembershipMapper.class,
        }
)
public interface ReviewerTaskStatusMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "reviewer", source = "projectMembership", qualifiedByName = "ToInfoMembershipDtoOrNull")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "status", source = "taskStatus.statusName")
    ReviewerTaskStatusResponseDto toReviewerTaskStatusDto(ReviewerTaskStatusEntity entity);


    @Named("ToReviewerTaskStatusResponseDtoOrNull")
    default ReviewerTaskStatusResponseDto toReviewerTaskStatusResponseDto(ReviewerTaskStatusEntity entity) {
        return entity != null ? toReviewerTaskStatusDto(entity) : null;
    }

}
