package by.pirog.ReverseGanttChart.configuration;

import by.pirog.ReverseGanttChart.security.DualCookieAuthenticationConverter;
import by.pirog.ReverseGanttChart.security.detailsService.TokenUserDetailsService;
import by.pirog.ReverseGanttChart.security.filter.LoginIntoProjectCookieAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.filter.LoginUsernamePasswordAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.strategy.TokenCookieSessionAuthenticationStrategy;
import by.pirog.ReverseGanttChart.security.token.Token;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;

import java.util.function.Function;

public class TokenCookieAuthenticationConfigurer
        extends AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer, HttpSecurity> {

    private final ObjectMapper objectMapper;
    Function<String, Token> tokenCookieStringDeserializer;
    private final UserRepository userRepository;
    private TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;

    private LoginIntoProjectCookieAuthenticationFilter loginIntoProjectCookieAuthenticationFilter;
    private LogoutFilter logoutFilter;

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

        var cookieAuthenticationFilter = getAuthenticationFilter(authenticationManager);

        var logoutFilter = this.logoutFilter;
        http.addFilterAfter(logoutFilter, CsrfFilter.class);
        http.addFilterAfter(usernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(cookieAuthenticationFilter, CsrfFilter.class);
        http.addFilterAfter(loginIntoProjectCookieAuthenticationFilter, CsrfFilter.class);
        http.authenticationProvider(authenticationProvider);
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        var cookieAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                new DualCookieAuthenticationConverter(this.tokenCookieStringDeserializer)
        );

        cookieAuthenticationFilter.setSuccessHandler((request, response, auth) -> {
            // Todo - здесь нужно доделать в случае удачной конвертации
        });
        cookieAuthenticationFilter.setFailureHandler((request, response, exception) -> {
            // Todo здесь нужно доделать ошибку
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        });
        return cookieAuthenticationFilter;
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


    public TokenCookieAuthenticationConfigurer loginIntoProjectCookieAuthenticationFilter
            (LoginIntoProjectCookieAuthenticationFilter loginIntoProjectCookieAuthenticationFilter) {
        this.loginIntoProjectCookieAuthenticationFilter = loginIntoProjectCookieAuthenticationFilter;
        return this;
    }

    public TokenCookieAuthenticationConfigurer logoutFilter(LogoutFilter logoutFilter) {
        this.logoutFilter = logoutFilter;
        return this;
    }
}
