package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ResponseUserMembershipMeDto;
import by.pirog.ReverseGanttChart.security.enums.UserRole;
import by.pirog.ReverseGanttChart.service.projectMembership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;

    @GetMapping("/me")
    public ResponseEntity<ResponseUserMembershipMeDto> userMembershipMe(){
        ResponseUserMembershipMeDto response = this.membershipService.getInfoAboutCurrentMembership();
        return ResponseEntity.ok(response);
    }

    // Возвращает все проекты, в которых участвует пользователь
    @GetMapping
    public ResponseEntity<List<ResponseUserMembershipMeDto>> getAllUserMemberships(){
        List<ResponseUserMembershipMeDto> response = this.membershipService.getAllUserMemberships();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/action/add")
    public ResponseEntity<Void> addProjectMembership(@RequestBody AddProjectMembershipDto projectMembershipDto) {
        this.membershipService.addMembershipToProjectByEmail(projectMembershipDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<InfoProjectMembershipDto>> getAllProjectMembershipsInProject() {
        List<InfoProjectMembershipDto> response = this.membershipService.findAllMembershipByProjectId();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/action/remove")
    public ResponseEntity<Void> removeProjectMembership(
                                                        @RequestParam("email") String email) {
        this.membershipService.removeMembershipFromProjectByEmail(email);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/action/updateAuthority")
    public ResponseEntity<Void> updateProjectMembershipAuthority(@RequestParam("email") String email,
                                                                 @RequestParam("role") UserRole role) {
        this.membershipService.updateProjectMembershipAuthority(email, role);

        return ResponseEntity.ok().build();
    }
}
