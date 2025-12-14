package by.pirog.ReverseGanttChart.security.provider;

import by.pirog.ReverseGanttChart.security.token.CustomAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class DualPreAuthenticatedAuthenticationProvider extends PreAuthenticatedAuthenticationProvider {

    private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> preAuthenticatedUserDetailsService;

    private PreAuthenticatedAuthenticationProvider delegateAuthenticationProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        if (authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getCredentials() == null) {
            return null;
        }
        UserDetails userDetails = this.preAuthenticatedUserDetailsService
                .loadUserDetails((PreAuthenticatedAuthenticationToken) authentication);
        var token = new CustomAuthenticationToken(userDetails, authentication);
        token.setDetails(userDetails);
        token.setAuthenticated(true);
        return token;
    }

    public DualPreAuthenticatedAuthenticationProvider delegateAuthenticationProvider
            (PreAuthenticatedAuthenticationProvider delegateAuthenticationProvider) {
        this.delegateAuthenticationProvider = delegateAuthenticationProvider;
        return this;
    }

    public DualPreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider
            (AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>
                     preAuthenticatedUserDetailsService) {
        this.preAuthenticatedUserDetailsService = preAuthenticatedUserDetailsService;
        return this;
    }
}
