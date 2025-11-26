package by.pirog.ReverseGanttChart.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix="app.security.token")
@Data
public class TokenBlacklistProperties {

    @JsonProperty("blacklist-token-project-prefix")
    private String blacklistTokenAuthPrefix = "blacklist:project:";

    @JsonProperty("blacklist-token-project-prefix")
    private String blacklistTokenProjectPrefix = "blacklist:project:";

    @JsonProperty("blacklist-token-project-ttl")
    private Duration blacklistTokenProjectTtl = Duration.ofHours(1);

    @JsonProperty("blacklist-token-auth-ttl")
    private Duration blacklistTokenAuthTtl = Duration.ofHours(1);
}
