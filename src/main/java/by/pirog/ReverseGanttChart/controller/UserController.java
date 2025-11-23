package by.pirog.ReverseGanttChart.controller;

import by.pirog.ReverseGanttChart.dto.UserResponse;
import by.pirog.ReverseGanttChart.security.token.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {

    @GetMapping
    public ResponseEntity<?> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            Token yourToken = (Token) authentication.getCredentials();

            return ResponseEntity.ok(Map.of(
                    "email", authentication.getName(),
                    "tokenId", yourToken.id(),
                    "authorities", yourToken.authorities(),
                    "expiresAt", yourToken.expiresAt()
            ));
        }

        return ResponseEntity.badRequest().body("Not JWT authentication");
    }
}
