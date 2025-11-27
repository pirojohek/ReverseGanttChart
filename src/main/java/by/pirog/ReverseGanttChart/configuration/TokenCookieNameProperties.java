package by.pirog.ReverseGanttChart.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="app.security.cookie")
@Data
public class TokenCookieNameProperties {

    @JsonProperty("project-name")
    private String projectCookieName;

    @JsonProperty("auth-name")
    private String authCookieName;
}
