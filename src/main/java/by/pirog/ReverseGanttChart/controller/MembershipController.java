package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.membershipDto.AddProjectMembershipDto;
import by.pirog.ReverseGanttChart.dto.membershipDto.InfoProjectMembershipDto;
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

    @PostMapping("/action/add")
    public ResponseEntity<Void> addProjectMembership(@RequestBody AddProjectMembershipDto projectMembershipDto,
                                                     @RequestParam("projectId") Long projectId) {
        this.membershipService.addMembershipToProjectByEmail(projectMembershipDto, projectId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<InfoProjectMembershipDto>> getAllProjectMemberships(@RequestParam("projectId") Long projectId) {
        List<InfoProjectMembershipDto> response = this.membershipService.findAllMembershipByProjectId(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @DeleteMapping("/action/remove")
    public ResponseEntity<Void> removeProjectMembership(@RequestParam("projectId") Long projectId,
                                                        @RequestParam("email") String email) {
        this.membershipService.removeMembershipFromProjectByEmail(email, projectId);

        return ResponseEntity.ok().build();
    }
}
