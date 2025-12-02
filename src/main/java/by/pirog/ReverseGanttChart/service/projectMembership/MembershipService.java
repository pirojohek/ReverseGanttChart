package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.security.enums.UserRole;

import java.util.List;

public interface MembershipService {

    void addMembershipToProjectByEmail(AddProjectMembershipDto addProjectMembershipDto);

    void removeMembershipFromProjectByEmail(String email);

    List<InfoProjectMembershipDto> findAllMembershipByProjectId();

    void updateProjectMembershipAuthority(String email, UserRole userRole);
}
