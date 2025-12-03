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

    private String blacklistTokenAuthPrefix = "blacklist:auth:";

    private String blacklistTokenProjectPrefix = "blacklist:project:";

    private Duration blacklistTokenProjectTtl = Duration.ofHours(1);

    private Duration blacklistTokenAuthTtl = Duration.ofHours(1);
}
