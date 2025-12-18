package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.passwordDto.NewPasswordDto;
import by.pirog.ReverseGanttChart.service.password.UserPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class ResetPasswordController {

    private final UserPasswordService userPasswordService;

    // Можно отправлять либо почту, либо имя пользователя
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestParam("username") String username) {
        this.userPasswordService.sendResetPassword(username);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-password")
    public ResponseEntity<Void> resetPassword(@RequestBody NewPasswordDto dto) {
        this.userPasswordService.updatePassword(dto);
        return ResponseEntity.ok().build();
    }
}
