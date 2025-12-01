package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;

import java.util.List;

public interface MembershipService {

    void addMembershipToProjectByEmail(AddProjectMembershipDto addProjectMembershipDto, Long projectId);

    void removeMembershipFromProjectByEmail(String email, Long projectId);

    List<InfoProjectMembershipDto> findAllMembershipByProjectId(Long projectId);
}
