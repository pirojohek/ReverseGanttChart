package by.pirog.ReverseGanttChart.security.detailsService;

import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.security.user.CustomUserDetails;
import by.pirog.ReverseGanttChart.storage.entity.UserEntity;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
public class TokenUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        Token authentication = (Token) token.getCredentials();
        // Здесь по идее тоже интересный момент, потому что нужно искать и по email и по username
        UserEntity userEntity = userRepository.findByUsername(authentication.subject())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        return new CustomUserDetails(userEntity, authentication);
    }
}
