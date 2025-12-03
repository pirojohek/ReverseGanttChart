package by.pirog.ReverseGanttChart.security.provider;

import by.pirog.ReverseGanttChart.security.token.DualPreAuthenticatedAuthenticationToken;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class DualPreAuthenticatedAuthenticationProvider extends PreAuthenticatedAuthenticationProvider {

    private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> preAuthenticatedUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        if (!(authentication instanceof DualPreAuthenticatedAuthenticationToken)){
            return super.authenticate(authentication);
        }
        if (authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getCredentials() == null) {
            return null;
        }
        UserDetails userDetails = this.preAuthenticatedUserDetailsService
                .loadUserDetails((PreAuthenticatedAuthenticationToken) authentication);
        var token = (DualPreAuthenticatedAuthenticationToken) authentication;
        token.setDetails(userDetails);
        token.setAuthenticated(true);

        return token;
    }

    public void setPreAuthenticatedUserDetailsService(
            AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> uds) {
        this.preAuthenticatedUserDetailsService = uds;
    }

}
