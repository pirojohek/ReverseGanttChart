package by.pirog.ReverseGanttChart.security.token;

import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DualPreAuthenticatedAuthenticationToken extends PreAuthenticatedAuthenticationToken {
    @Getter
    private final Authentication authenticationAuth;
    @Getter
    private final Authentication projectAuth;

    @Getter
    private List<GrantedAuthority> grantedAuthorities;

    public DualPreAuthenticatedAuthenticationToken(Authentication authentication, Authentication projectAuthentication) {
        super(authentication.getPrincipal(), authentication.getCredentials(),
                generateGrantedAuthorities(authentication, projectAuthentication));
        this.authenticationAuth = authentication;
        this.projectAuth = projectAuthentication;
        this.grantedAuthorities = generateGrantedAuthorities(authentication, projectAuthentication);
    }

    public DualPreAuthenticatedAuthenticationToken(UserDetails userDetails, Authentication authentication, Authentication projectAuthentication){
        super(userDetails, authentication.getCredentials(),
                generateGrantedAuthorities(authentication, projectAuthentication));
        this.authenticationAuth = authentication;
        this.projectAuth = projectAuthentication;
        this.grantedAuthorities = generateGrantedAuthorities(authentication, projectAuthentication);
    }

    private static List<GrantedAuthority> generateGrantedAuthorities(Authentication authentication, Authentication projectAuthentication) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.addAll(authentication.getAuthorities());
        grantedAuthorities.addAll(projectAuthentication.getAuthorities());
        return grantedAuthorities;
    }

    public Long getProjectId(){
        return ((Token) this.projectAuth.getCredentials()).projectId();
    }



}
