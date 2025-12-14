package by.pirog.ReverseGanttChart.security.token;

import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomAuthenticationToken extends PreAuthenticatedAuthenticationToken {

    private Long projectId;
    @Getter
    private UserDetails userDetails;
    @Getter
    private Token authenticationToken;

    private Authentication authentication;

    private List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    public CustomAuthenticationToken(Authentication authentication) {
        super(authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());
        Token token = (Token) authentication.getCredentials();
        this.projectId = token.projectId();
        this.grantedAuthorities.addAll(authentication.getAuthorities());
        this.authentication = authentication;
        this.authenticationToken = token;
    }

    public CustomAuthenticationToken(UserDetails customUserDetails, Authentication authentication) {
        super(authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());
        Token token = (Token) authentication.getCredentials();
        this.userDetails = customUserDetails;
        this.projectId = token.projectId();
        this.authentication = authentication;
        this.authenticationToken = token;
        this.grantedAuthorities.addAll(authentication.getAuthorities());
    }

    public Long getProjectId() {
        return projectId;
    }
}
