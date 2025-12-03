package by.pirog.ReverseGanttChart.configuration;

import by.pirog.ReverseGanttChart.security.deserializer.TokenCookieJweStringDeserializer;
import by.pirog.ReverseGanttChart.security.detailsService.CustomUserDetailsService;
import by.pirog.ReverseGanttChart.security.enums.UserRole;
import by.pirog.ReverseGanttChart.security.factory.AuthorizationManagerFactory;
import by.pirog.ReverseGanttChart.security.filter.LoginIntoProjectCookieAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.securityService.authService.ProjectSecurityService;
import by.pirog.ReverseGanttChart.security.securityService.blacklistService.RedisTokenBlacklistService;
import by.pirog.ReverseGanttChart.security.securityService.tokenService.TokenService;
import by.pirog.ReverseGanttChart.security.serializer.TokenCookieJweStringSerializer;
import by.pirog.ReverseGanttChart.security.strategy.TokenCookieSessionAuthenticationStrategy;
import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;


@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    private final AuthorizationManagerFactory authorizationManagerFactory;

    @Bean
    public TokenCookieJweStringSerializer tokenCookieJweStringSerializer(
            @Value("${jwt.cookie-token-key}") String jwtCookieTokenKey
    ) throws Exception {
        return new TokenCookieJweStringSerializer(new DirectEncrypter(
                OctetSequenceKey.parse(jwtCookieTokenKey)
        ));
    }


    @Bean
    public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
            @Value("${jwt.cookie-token-key}") String jwtCookieTokenKey,
            UserRepository userRepository,
            TokenService tokenService,
            LoginIntoProjectCookieAuthenticationFilter loginIntoProjectCookieAuthenticationFilter,
            LogoutFilter logoutFilter,
            RedisTokenBlacklistService redisTokenBlacklistService
    ) throws Exception {
        return new TokenCookieAuthenticationConfigurer(objectMapper, userRepository)
                .tokenCookieStringDeserializer(
                        new TokenCookieJweStringDeserializer(new DirectDecrypter(
                                OctetSequenceKey.parse(jwtCookieTokenKey)
                        ))
                )
                .tokenCookieAuthenticationStrategy(new TokenCookieSessionAuthenticationStrategy(tokenService))
                .loginIntoProjectCookieAuthenticationFilter(loginIntoProjectCookieAuthenticationFilter)
                .logoutFilter(logoutFilter)
                .redisTokenBlacklistService(redisTokenBlacklistService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   TokenCookieJweStringSerializer tokenCookieJweStringSerializer,
                                                   TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {



        http.csrf(csrf -> csrf.disable())
                .logout(logoutRequest -> logoutRequest.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasicAuthentication -> httpBasicAuthentication.disable())
                .userDetailsService(userDetailsService)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/errors/**").permitAll()
                                .requestMatchers("/api/project/info/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/project/action/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.ADMIN.getAuthority()))
                                .requestMatchers("/api/project/*").authenticated()
                                .requestMatchers("/api/task/info/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/task/setStudentStatus")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.STUDENT.getAuthority()))
                                .requestMatchers("/api/task/setReviewerStatus")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.REVIEWER.getAuthority()))
                                .requestMatchers("/api/task/action/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.PLANNER.getAuthority()))
                                .requestMatchers("/api/membership/action/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.PLANNER.getAuthority()))
                                .requestMatchers("/api/membership/getAll")
                                .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/membership/me").access(authorizationManagerFactory.hasProjectAccess())
                                .anyRequest().authenticated());
        http.apply(tokenCookieAuthenticationConfigurer);
        return http.build();
    }
}
