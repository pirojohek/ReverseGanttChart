package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.RegisteredResponseDto;
import by.pirog.ReverseGanttChart.dto.RegistrationDto;
import by.pirog.ReverseGanttChart.service.registration.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final RegisterService registerService;

    // Todo - обработать ошибку с форматом email, она есть, но неправильно обрабатывается
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDto registrationDto,
                                      BindingResult bindingResult) throws BindException {


        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }
            throw new BindException(bindingResult);
        }

        RegisteredResponseDto response = registerService.register(registrationDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

}
