package by.pirog.ReverseGanttChart.security.securityService.blacklistService;

import by.pirog.ReverseGanttChart.configuration.TokenBlacklistProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisTokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenBlacklistProperties tokenBlacklistProperties;

    public void addToBlackListAuthToken(String token){
        try {
            String tokenKey = getAuthTokenKey(token);
            Instant now = Instant.now();

            TokenBlacklistInfo tokenBlacklistInfo = TokenBlacklistInfo.builder()
                    .blacklistedDate(now)
                    .build();

            redisTemplate.opsForValue().set(
                    tokenKey,
                    tokenBlacklistInfo,
                    tokenBlacklistProperties.getBlacklistTokenAuthTtl()
            );

            log.info(" Токен добавлен в blacklist. Ключ: {}",
                    tokenKey);
        } catch (Exception e) {
            log.error("Ошибка при добавлении токена в blacklist: {}", e.getMessage());
        }
    }

    public boolean isAuthTokenBlacklisted(String token){
        try {
            String tokenKey = getAuthTokenKey(token);
            Boolean exists = redisTemplate.hasKey(tokenKey);

            boolean blacklisted = exists != null && exists;

            return blacklisted;
        } catch (Exception e) {
            log.error("Ошибка при проверке токена в blacklist: {}", e.getMessage());
            return true;
        }
    }

    public void addToBlacklistProjectToken(String token) {
        try {
            String tokenKey = getProjectTokenKey(token);
            Instant now = Instant.now();

            TokenBlacklistInfo tokenBlacklistInfo = TokenBlacklistInfo.builder()
                    .blacklistedDate(now)
                    .build();

            redisTemplate.opsForValue().set(
                    tokenKey,
                    tokenBlacklistInfo,
                    tokenBlacklistProperties.getBlacklistTokenProjectTtl()
            );

            log.info(" Токен добавлен в blacklist. Ключ: {}",
                    tokenKey);
        } catch (Exception e) {
            log.error("Ошибка при добавлении токена в blacklist: {}", e.getMessage());
        }
    }

    public boolean isProjectTokenBlacklisted(String token){
        try {
            String tokenKey = getProjectTokenKey(token);
            Boolean exists = redisTemplate.hasKey(tokenKey);

            boolean blacklisted = exists != null && exists;

            return blacklisted;
        } catch (Exception e) {
            log.error("Ошибка при проверке токена в blacklist: {}", e.getMessage());
            return true;
        }
    }


    private String getProjectTokenKey(String token) {
        String tokenHash = DigestUtils.sha256Hex(token);
        return tokenBlacklistProperties.getBlacklistTokenProjectPrefix() + tokenHash;
    }

    private String getAuthTokenKey(String token) {
        String tokenHash = DigestUtils.sha256Hex(token);
        return tokenBlacklistProperties.getBlacklistTokenAuthPrefix() + tokenHash;
    }
}
