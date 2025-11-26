package by.pirog.ReverseGanttChart.configuration;

import by.pirog.ReverseGanttChart.security.filter.LoginIntoProjectCookieAuthenticationFilter;
import by.pirog.ReverseGanttChart.security.handler.BlacklistLogoutHandler;
import by.pirog.ReverseGanttChart.security.handler.CleaningJwtCookiesLogoutHandler;
import by.pirog.ReverseGanttChart.security.handler.JwtLogoutSuccessHandler;
import by.pirog.ReverseGanttChart.security.securityService.blacklistService.RedisTokenBlacklistService;
import by.pirog.ReverseGanttChart.security.securityService.tokenService.TokenService;
import by.pirog.ReverseGanttChart.service.projectMembership.DefaultProjectMembershipService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class FilterBeansConfiguration {

    @Bean
    public LoginIntoProjectCookieAuthenticationFilter loginIntoProjectCookieAuthenticationFilter
            (DefaultProjectMembershipService projectMembershipService, TokenService tokenService) {
        return new LoginIntoProjectCookieAuthenticationFilter(projectMembershipService, tokenService);
    }

    @Bean
    public LogoutFilter logoutFilter(RedisTokenBlacklistService tokenBlacklistService) {

        var blacklistLogoutHandler = new BlacklistLogoutHandler(tokenBlacklistService);
        var cleaningJwtCookieLogoutHandler = new CleaningJwtCookiesLogoutHandler();
        var jwtLogoutSuccessHandler = new JwtLogoutSuccessHandler();

        LogoutFilter logoutFilter = new LogoutFilter(jwtLogoutSuccessHandler,
                blacklistLogoutHandler,
                cleaningJwtCookieLogoutHandler);

        logoutFilter.setLogoutRequestMatcher(
                new AntPathRequestMatcher("/api/auth/logout", "POST")
        );

        return logoutFilter;
    }



}
