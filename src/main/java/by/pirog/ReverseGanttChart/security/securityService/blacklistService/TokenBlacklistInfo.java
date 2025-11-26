package by.pirog.ReverseGanttChart.security.securityService.blacklistService;


import lombok.*;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklistInfo {
    private Instant blacklistedDate;
    private Instant expiredAtDate;

}
