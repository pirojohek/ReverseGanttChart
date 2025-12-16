package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.ProjectMembershipUserProjectsResponseDto;
import by.pirog.ReverseGanttChart.enums.UserRole;
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


    // Возвращает все проекты, в которых участвует пользователь
    @GetMapping
    public ResponseEntity<List<ProjectMembershipUserProjectsResponseDto>> getAllUserMemberships(){
        List<ProjectMembershipUserProjectsResponseDto> response = this.membershipService.getAllUserMemberships();

        return ResponseEntity.ok(response);
    }

    // Todo перед продом запретить этот эндпоинт
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
                                                        @RequestParam("projectUsername") String projectUsername) {
        this.membershipService.removeMembershipFromProjectByEmail(projectUsername);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/action/updateAuthority")
    public ResponseEntity<Void> updateProjectMembershipAuthority(@RequestParam("projectUsername") String projectUsername,
                                                                 @RequestParam("role") UserRole role) {
        this.membershipService.updateProjectMembershipAuthority(projectUsername, role);

        return ResponseEntity.ok().build();
    }
}
