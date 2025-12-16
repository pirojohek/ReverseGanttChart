package by.pirog.ReverseGanttChart.mapper;


import by.pirog.ReverseGanttChart.dto.projectComponentDto.*;
import by.pirog.ReverseGanttChart.mapper.qualifier.DateTimeQualifier;
import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectEntity;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
                CommentMapper.class,
                DateTimeQualifier.class,
                ProjectMembershipMapper.class,
                ReviewerTaskStatusMapper.class,
                StudentTaskStatusMapper.class,
                TaskMakerMapper.class
        })
public interface ProjectComponentMapper {

    // ===================== CREATED RESPONSE ==================
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "parentId", source = "projectComponentParent.id")  // Todo - есть ощущение, что здесь все упадет
    @Mapping(target = "deadline", source = "deadline", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "creator", source = "creator.projectUsername")
    @Mapping(target = "role", source = "creator.userRole.roleName")
    CreatedProjectComponentDto toCreatedProjectComponentDto(ProjectComponentEntity entity);


    // ==================== UPDATED RESPONSE ======================
    @Mapping(target = "componentId", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "deadline", source = "deadline", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "InstantToLocalDateTime")
    UpdatedProjectComponentResponseDto toUpdatedProjectComponentDto(ProjectComponentEntity entity);

    // ================== FULL RESPONSE WITH HIERARCHY ==============
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "parentId", source = "projectComponentParent.id")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "deadline", source = "deadline", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "InstantToLocalDateTime")
    @Mapping(target = "creator", source = "creator", qualifiedByName = "ToInfoMembershipDtoOrNull")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "taskStatus", source = "studentTaskStatus", qualifiedByName = "ToStudentTaskStatusDtoOrNull")
    @Mapping(target = "reviewerTaskStatus", source = "reviewerTaskStatus", qualifiedByName = "ToReviewerTaskStatusResponseDtoOrNull")
    @Mapping(target = "taskMakers", source = "taskMakers", qualifiedByName = "TaskMakerSetToList")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "CommentSetToList")
    @Mapping(target = "globalTaskStatus", source = "globalTaskStatus")
    ProjectComponentResponseDto toProjectComponentResponseDto(ProjectComponentEntity entity);

    default List<ProjectComponentResponseDto> toHierarchyDtoList(List<ProjectComponentEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        var dtoMap = new HashMap<Long, ProjectComponentResponseDto>();
        for (ProjectComponentEntity entity : entities) {
            dtoMap.put(entity.getId(), toProjectComponentResponseDto(entity));
        }

        var roots = new ArrayList<ProjectComponentResponseDto>();
        for (var entity : entities) {
            var dto = dtoMap.get(entity.getId());

            if (entity.getProjectComponentParent() != null) {
                var parentDto = dtoMap.get(entity.getProjectComponentParent().getId());
                if (parentDto.getChildren() == null) {
                    parentDto.setChildren(new ArrayList<>());
                }
                parentDto.getChildren().add(dto);
            } else {
                roots.add(dto);
            }
        }

        roots.sort(Comparator.comparing(ProjectComponentResponseDto::getCreatedAt));
        return roots;
    }

    // ======================= ENTITY BUILDING ==================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "projectComponentParent", source = "parent")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "studentTaskStatus", ignore = true)
    @Mapping(target = "deadline", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "reviewerTaskStatus", ignore = true)
    @Mapping(target = "taskMakers", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "projectComponentChildren", ignore = true)
    @Mapping(target = "globalTaskStatus", ignore = true)
    ProjectComponentEntity toEntity(
            CreateProjectComponentDto dto,
            ProjectMembershipEntity creator,
            ProjectEntity project,
            ProjectComponentEntity parent
    );

    @AfterMapping
    default void setDatesFromDto(
            CreateProjectComponentDto dto,
            @MappingTarget ProjectComponentEntity entity
    ) {
        if (dto != null) {
            if (dto.deadlineDate() != null && dto.deadlineTime() != null) {
                entity.setDeadlineFromLocalDateAndTime(dto.deadlineDate(), dto.deadlineTime());
            } else {
                entity.setDeadlineFromLocalDate(dto.deadlineDate());
            }

            if (dto.startDate() != null && dto.startTime() != null) {
                entity.setStartDateFromLocalDateAndTime(dto.startDate(), dto.startTime());
            } else {
                entity.setStartDateFromLocalDate(dto.startDate());
            }
        }
    }

    // ============================= UPDATE ENTITY ======================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "deadline", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "projectComponentParent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "studentTaskStatus", ignore = true)
    @Mapping(target = "reviewerTaskStatus", ignore = true)
    @Mapping(target = "taskMakers", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "projectComponentChildren", ignore = true)
    ProjectComponentEntity updateEntityFromDto(
            UpdateProjectComponentRequestDto dto,
            @MappingTarget ProjectComponentEntity entity
    );

    @AfterMapping
    default void handleUpdateDates(
            UpdateProjectComponentRequestDto dto,
            @MappingTarget ProjectComponentEntity entity
    ){
        if (dto == null) {
            return;
        }

        if (dto.hasDeadlineDate() && dto.hasDeadlineTime()) {
            entity.setDeadlineFromLocalDateAndTime(dto.deadlineDate(), dto.deadlineTime());
        } else if (dto.hasDeadlineDate() && !dto.hasDeadlineTime()) {
            entity.setDeadlineFromLocalDate(dto.deadlineDate());
        }else if (!dto.hasDeadlineDate() && dto.hasDeadlineTime()) {

            if (entity.getDeadlineAsLocalDateTime() != null) {
                LocalDate existingDate = entity.getDeadlineAsLocalDateTime().toLocalDate();
                entity.setDeadlineFromLocalDateAndTime(existingDate, dto.deadlineTime());
            }
        }

        if (dto.hasStartDate() && dto.hasStartTime()) {
            entity.setStartDateFromLocalDateAndTime(dto.startDate(), dto.startTime());
        }
        else if (dto.hasStartDate() && !dto.hasStartTime()) {
            entity.setStartDateFromLocalDate(dto.startDate());
        }
        else if (!dto.hasStartDate() && dto.hasStartTime()) {
            if (entity.getStartDateAsLocalDateTime() != null) {
                LocalDate existingDate = entity.getStartDateAsLocalDateTime().toLocalDate();
                entity.setStartDateFromLocalDateAndTime(existingDate, dto.startTime());
            }
        }

    }

}
