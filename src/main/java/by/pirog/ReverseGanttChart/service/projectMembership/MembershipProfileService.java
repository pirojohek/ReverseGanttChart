package by.pirog.ReverseGanttChart.service.projectMembership;

import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipProfileResponseDto;
import by.pirog.ReverseGanttChart.exception.UserIsNotMemberInProjectException;
import by.pirog.ReverseGanttChart.mapper.ProjectMembershipMapper;
import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.storage.entity.ProjectMembershipEntity;
import by.pirog.ReverseGanttChart.storage.repository.ProjectMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipProfileService {

    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectMembershipMapper projectMembershipMapper;

    public ProjectMembershipProfileResponseDto getMembershipProfileInfo(){
        ProjectMembershipEntity entity = getCurrentProjectMembership();

        return projectMembershipMapper.toProjectMembershipProfileResponseDto(entity);
    }

    public void leaveProject(){
        this.projectMembershipRepository.delete(getCurrentProjectMembership());
    }

    public ProjectMembershipProfileResponseDto setNewProjectUsername(String username){
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if(projectMembershipRepository.findProjectMembershipByUsernameAndProjectId(username, token.getProjectId()).isPresent()){
            throw new IllegalArgumentException("Username is already in use");
        }

        ProjectMembershipEntity entity = getCurrentProjectMembership();
        entity.setProjectUsername(username);
        this.projectMembershipRepository.save(entity);

        return projectMembershipMapper.toProjectMembershipProfileResponseDto(entity);
    }


    private ProjectMembershipEntity getCurrentProjectMembership(){
        var token = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.projectMembershipRepository.findByUserEmailAndProjectId(user.getEmail(), token.getProjectId())
                .orElseThrow(() -> new UserIsNotMemberInProjectException("User with email " + user.getEmail() + " not found"));
    }
}
