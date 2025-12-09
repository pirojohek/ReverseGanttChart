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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition",
                "Set-Cookie",
                "Set-Cookie2"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
            @Value("${jwt.cookie-token-key}") String jwtCookieTokenKey,
            UserRepository userRepository,
            TokenService tokenService,
            LoginIntoProjectCookieAuthenticationFilter loginIntoProjectCookieAuthenticationFilter,
            LogoutFilter logoutFilter,
            RedisTokenBlacklistService redisTokenBlacklistService,
            TokenCookieNameProperties tokenCookieNameProperties
    ) throws Exception {
        return new TokenCookieAuthenticationConfigurer(objectMapper, userRepository, tokenCookieNameProperties)
                .tokenCookieStringDeserializer(
                        new TokenCookieJweStringDeserializer(new DirectDecrypter(
                                OctetSequenceKey.parse(jwtCookieTokenKey)
                        ))
                )
                .tokenCookieAuthenticationStrategy(new TokenCookieSessionAuthenticationStrategy(tokenService))
                .loginIntoProjectCookieAuthenticationFilter(loginIntoProjectCookieAuthenticationFilter)
                .logoutFilter(logoutFilter)
                .redisTokenBlacklistService(redisTokenBlacklistService)
                .permitAllForPatterns("/api/auth/register", "/api/auth/login", "/api/auth/logout", "/errors/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   TokenCookieJweStringSerializer tokenCookieJweStringSerializer,
                                                   TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {



        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(logoutRequest -> logoutRequest.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasicAuthentication -> httpBasicAuthentication.disable())
                .userDetailsService(userDetailsService)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                                .requestMatchers("/api/auth/logout").permitAll()
                                .requestMatchers("/errors/**").permitAll()
                                .requestMatchers("/api/project/info/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/project/action/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.ADMIN.getAuthority()))
                                .requestMatchers("/api/project/*").authenticated()
                                .requestMatchers("/api/membership/action/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.PLANNER.getAuthority()))
                                .requestMatchers("/api/membership/getAll")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/membership/me").access(authorizationManagerFactory.hasProjectAccess())

                                .requestMatchers("/api/projectComponent/action/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.PLANNER.getAuthority()))
                                .requestMatchers("/api/projectComponent")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/projectComponent/*")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.VIEWER.getAuthority()))
                                .requestMatchers("/api/taskMakers/**")
                                    .access(authorizationManagerFactory.hasProjectAccessWithMinRole(UserRole.REVIEWER.getAuthority()))
                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.getWriter().write(
                                            "{\"error\": \"Authentication required\"}"
                                    );
                                }));
        http.apply(tokenCookieAuthenticationConfigurer);
        return http.build();
    }
}
