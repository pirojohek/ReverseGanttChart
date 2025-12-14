package by.pirog.ReverseGanttChart.mapper;

import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipProfileResponseDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipUserProjectsResponseDto;
import by.pirog.ReverseGanttChart.mapper.qualifier.DateTimeQualifier;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring",
        uses = {
                DateTimeQualifier.class
        })
public interface ProjectMembershipMapper {

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "userRole", source = "userRole.roleName")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "username", source = "projectUsername")
    InfoProjectMembershipDto toInfoProjectMembershipDto(ProjectMembershipEntity entity);


    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "userRole.roleName")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "username", source = "projectUsername")
    ProjectMembershipProfileResponseDto toProjectMembershipProfileResponseDto(ProjectMembershipEntity entity);


    // Todo если появится ProjectStatus, не забыть добавить
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "userRole.roleName")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "username", source = "projectUsername")
    @Mapping(target = "projectName", source = "project.projectName")
    @Mapping(target = "projectDescription", source = "project.projectDescription")
    @Mapping(target = "deadline", source = "project.deadline", qualifiedByName = "InstantToLocalDate")
    ProjectMembershipUserProjectsResponseDto toProjectMembershipUserProjectsResponseDto(ProjectMembershipEntity entity);


    @Named("ToInfoMembershipDtoOrNull")
    default InfoProjectMembershipDto toInfoOrNull(ProjectMembershipEntity entity) {
        return entity != null ? toInfoProjectMembershipDto(entity) : null;
    }
}
