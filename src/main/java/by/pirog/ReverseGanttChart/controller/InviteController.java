package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.invite.ChangeInviteRoleDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteResponseDto;
import by.pirog.ReverseGanttChart.service.email.EmailService;
import by.pirog.ReverseGanttChart.service.invite.ProjectInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invite")
public class InviteController {

    private final ProjectInviteService projectInviteService;

    @PostMapping("/action/send")
    public ResponseEntity<InviteResponseDto> sendInvite(
            @Valid @RequestBody InviteRequestDto inviteRequestDto
    ) {
        InviteResponseDto response = projectInviteService.sendInvitation(inviteRequestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptInvite(@RequestParam("token") String token){
        this.projectInviteService.acceptInvitation(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/action/all")
    public ResponseEntity<List<InviteResponseDto>> getAllInvites(){
        return ResponseEntity.ok(this.projectInviteService.getAllInvitesInProject());
    }

    @PostMapping("/action/resend")
    public ResponseEntity<InviteResponseDto> resendInvite(@RequestParam("email") String email){
        return ResponseEntity.ok(this.projectInviteService.resendInvite(email));
    }

    @PatchMapping("/action/changeRole")
    public ResponseEntity<InviteResponseDto> changeInviteRole(@RequestBody ChangeInviteRoleDto dto){
        return ResponseEntity.ok(this.projectInviteService.changeInviteRole(dto));
    }

    @DeleteMapping("/action/delete")
    public ResponseEntity<InviteResponseDto> deleteInvite(@RequestParam("email") String email){
        this.projectInviteService.deleteInvite(email);
        return ResponseEntity.noContent().build();
    }
}
