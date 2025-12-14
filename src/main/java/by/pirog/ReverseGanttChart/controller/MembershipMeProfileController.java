package by.pirog.ReverseGanttChart.controller;


import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipProfileResponseDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipUserProjectsResponseDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.UpdateProfileRequestDto;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipProfileService;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/membership/profile")
public class MembershipMeProfileController {
    // Todo - эндпоинт покинуть проект
    // Todo - эндпоинт получения информации о профиле
    // Todo - эндпоинт изменения ника

    private final MembershipProfileService membershipProfileService;

    @GetMapping("/me")
    public ResponseEntity<ProjectMembershipProfileResponseDto> userMembershipMe(){
        ProjectMembershipProfileResponseDto response = this.membershipProfileService.getMembershipProfileInfo();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Void> userMembershipLeaveProject(){
        this.membershipProfileService.leaveProject();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<ProjectMembershipProfileResponseDto> updateProfile
            (@RequestBody UpdateProfileRequestDto dto){
        ProjectMembershipProfileResponseDto response = this.membershipProfileService.setNewProjectUsername(dto.username());
        return ResponseEntity.ok(response);
    }
}
