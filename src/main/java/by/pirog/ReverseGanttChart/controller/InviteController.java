package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.invite.InviteRequestDto;
import by.pirog.ReverseGanttChart.dto.invite.InviteResponseDto;
import by.pirog.ReverseGanttChart.service.email.EmailService;
import by.pirog.ReverseGanttChart.service.invite.ProjectInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invite")
public class InviteController {

    private final ProjectInviteService projectInviteService;

    @PostMapping("/send")
    public ResponseEntity<InviteResponseDto> sendInvite(
            @Valid @RequestBody InviteRequestDto inviteRequestDto,
            BindingResult bindingResult
    ) {
        projectInviteService.sendInvitation(inviteRequestDto);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptInvite(@RequestParam("token") String token){
        this.projectInviteService.acceptInvitation(token);
        return ResponseEntity.ok(null);
    }
}
