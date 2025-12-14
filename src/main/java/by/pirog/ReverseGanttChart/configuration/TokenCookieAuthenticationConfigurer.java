package by.pirog.ReverseGanttChart.configuration;

import by.pirog.ReverseGanttChart.security.AuthenticationCookieAuthenticationConverter;
import by.pirog.ReverseGanttChart.security.detailsService.TokenUserDetailsService;
import by.pirog.ReverseGanttChart.security.filter.LoginIntoProjectCookieAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.filter.LoginUsernamePasswordAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.provider.DualPreAuthenticatedAuthenticationProvider;
import by.pirog.ReverseGanttChart.security.securityService.blacklistService.RedisTokenBlacklistService;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TokenCookieAuthenticationConfigurer
        extends AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer, HttpSecurity> {

    private final ObjectMapper objectMapper;
    private Function<String, Token> tokenCookieStringDeserializer;
    private final UserRepository userRepository;
    private TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;
    private LoginIntoProjectCookieAuthenticationFilter loginIntoProjectCookieAuthenticationFilter;
    private LogoutFilter logoutFilter;
    private RedisTokenBlacklistService redisTokenBlacklistService;


    private List<String> permitAllPaths = new ArrayList<>();

    private final TokenCookieNameProperties tokenCookieNameProperties;
    public TokenCookieAuthenticationConfigurer(ObjectMapper objectMapper, UserRepository userRepository
    ,TokenCookieNameProperties tokenCookieNameProperties) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.tokenCookieNameProperties = tokenCookieNameProperties;
    }

    public TokenCookieAuthenticationConfigurer permitAllForPatterns(String... patterns) {
        this.permitAllPaths.addAll(Arrays.asList(patterns));
        return this;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        var authenticationManager = http.getSharedObject(AuthenticationManager.class);

        // UsernameAuthenticationFilter
        var usernamePasswordAuthenticationFilter = new LoginUsernamePasswordAuthenticationFilter(objectMapper);
        usernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
        usernamePasswordAuthenticationFilter.setSessionAuthenticationStrategy(tokenCookieSessionAuthenticationStrategy);

        RequestMatcher publicPathsMatcher = new OrRequestMatcher(
                this.permitAllPaths.stream().map(AntPathRequestMatcher::new)
                        .toArray(RequestMatcher[]::new)
        );

        RequestMatcher protectedPathMatcher = new NegatedRequestMatcher(publicPathsMatcher);

        // AuthenticationProvider
        var delegateAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        delegateAuthenticationProvider.setPreAuthenticatedUserDetailsService(
                new TokenUserDetailsService(userRepository)
        );
        var authenticationProvider = new DualPreAuthenticatedAuthenticationProvider()
                .preAuthenticatedAuthenticationProvider(new TokenUserDetailsService(userRepository))
                .delegateAuthenticationProvider(delegateAuthenticationProvider);


        var cookieAuthenticationFilter = getAuthenticationFilter(authenticationManager);
        cookieAuthenticationFilter.setRequestMatcher(protectedPathMatcher);

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
                new AuthenticationCookieAuthenticationConverter(this.tokenCookieStringDeserializer, redisTokenBlacklistService, tokenCookieNameProperties)
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

    public TokenCookieAuthenticationConfigurer redisTokenBlacklistService(
            RedisTokenBlacklistService redisTokenBlacklistService) {
        this.redisTokenBlacklistService = redisTokenBlacklistService;
        return this;
    }
}
