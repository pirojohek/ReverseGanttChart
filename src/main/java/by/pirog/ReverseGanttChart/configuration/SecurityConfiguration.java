    package by.pirog.ReverseGanttChart.configuration;

    import by.pirog.ReverseGanttChart.security.TokenCookieAuthenticationConverter;
    import by.pirog.ReverseGanttChart.security.deserializer.TokenCookieJweStringDeserializer;
    import by.pirog.ReverseGanttChart.security.detailsService.CustomUserDetailsService;
    import by.pirog.ReverseGanttChart.security.filter.LoginUsernamePasswordAuthenticationFilter;
    import by.pirog.ReverseGanttChart.security.serializer.TokenCookieJweStringSerializer;
    import by.pirog.ReverseGanttChart.security.strategy.TokenCookieSessionAuthenticationStrategy;
    import by.pirog.ReverseGanttChart.storage.repository.UserRepository;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.nimbusds.jose.JOSEException;
    import com.nimbusds.jose.crypto.DirectDecrypter;
    import com.nimbusds.jose.crypto.DirectEncrypter;
    import com.nimbusds.jose.jwk.OctetSequenceKey;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.ProviderManager;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @RequiredArgsConstructor
    public class SecurityConfiguration {

        private final CustomUserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;

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
                TokenCookieJweStringSerializer serializer
        ) throws Exception {
            return new TokenCookieAuthenticationConfigurer(objectMapper, userRepository)
                    .tokenCookieStringDeserializer(
                            new TokenCookieJweStringDeserializer(new DirectDecrypter(
                                    OctetSequenceKey.parse(jwtCookieTokenKey)
                            ))
                    )
                    .tokenCookieAuthenticationStrategy(new TokenCookieSessionAuthenticationStrategy()
                            .authenticationSerializer(serializer));
        }


        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                       TokenCookieJweStringSerializer tokenCookieJweStringSerializer,
                                                       TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {

            http.csrf(csrf -> csrf.disable())
                    .userDetailsService(userDetailsService)
                    .sessionManagement(sessionManagement ->
                            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorizeRequests ->
                            authorizeRequests.requestMatchers("/api/auth/**").permitAll()
                                    .anyRequest().authenticated());
            http.apply(tokenCookieAuthenticationConfigurer);
            return http.build();
        }

    }
