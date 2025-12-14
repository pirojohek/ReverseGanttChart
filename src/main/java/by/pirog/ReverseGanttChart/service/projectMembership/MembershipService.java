package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipUserProjectsResponseDto;
import by.pirog.ReverseGanttChart.enums.UserRole;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;

import java.util.List;

public interface MembershipService {

    void addMembershipToProjectByEmail(AddProjectMembershipDto addProjectMembershipDto);

    void removeMembershipFromProjectByEmail(String email);

    List<InfoProjectMembershipDto> findAllMembershipByProjectId();

    void updateProjectMembershipAuthority(String email, UserRole userRole);

    List<ProjectMembershipUserProjectsResponseDto> getAllUserMemberships();

    ProjectMembershipEntity getCurrentProjectMembership();

    ProjectMembershipEntity getProjectMembershipByEmail(String email);

    void saveProjectMembership(ProjectMembershipEntity projectMembershipEntity);

}
