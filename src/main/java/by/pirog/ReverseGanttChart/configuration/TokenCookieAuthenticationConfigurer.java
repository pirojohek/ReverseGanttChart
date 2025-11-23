package by.pirog.ReverseGanttChart.configuration;

import by.pirog.ReverseGanttChart.security.TokenCookieAuthenticationConverter;
import by.pirog.ReverseGanttChart.security.detailsService.TokenUserDetailsService;
import by.pirog.ReverseGanttChart.security.filter.LoginUsernamePasswordAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.serializer.TokenCookieJweStringSerializer;
import by.pirog.ReverseGanttChart.security.strategy.TokenCookieSessionAuthenticationStrategy;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.csrf.CsrfFilter;

import java.util.function.Function;

public class TokenCookieAuthenticationConfigurer
        extends AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer, HttpSecurity> {

    private final ObjectMapper objectMapper;
    Function<String, Token> tokenCookieStringDeserializer;
    private final UserRepository userRepository;
    private TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;

    public TokenCookieAuthenticationConfigurer(ObjectMapper objectMapper, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        var authenticationManager = http.getSharedObject(AuthenticationManager.class);

        var usernamePasswordAuthenticationFilter = new LoginUsernamePasswordAuthenticationFilter(objectMapper);
        usernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
        usernamePasswordAuthenticationFilter.setSessionAuthenticationStrategy(tokenCookieSessionAuthenticationStrategy);


        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(
                new TokenUserDetailsService(userRepository)
        );

        var cookieAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                new TokenCookieAuthenticationConverter(this.tokenCookieStringDeserializer)
        );
        cookieAuthenticationFilter.setSuccessHandler((request, response, auth) -> {
        });
        cookieAuthenticationFilter.setFailureHandler((request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        });


        http.addFilterAfter(usernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(cookieAuthenticationFilter, CsrfFilter.class);
        http.authenticationProvider(authenticationProvider);
    }


    public TokenCookieAuthenticationConfigurer tokenCookieStringDeserializer
            (Function<String, Token> tokenCookieStringDeserializer) {
        this.tokenCookieStringDeserializer = tokenCookieStringDeserializer;
        return this;
    }

    public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationStrategy(
            TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy
    ) {
        this.tokenCookieSessionAuthenticationStrategy = tokenCookieSessionAuthenticationStrategy;
        return this;
    }

}
