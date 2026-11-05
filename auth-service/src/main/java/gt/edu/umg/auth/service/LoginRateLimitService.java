package gt.edu.umg.auth.service;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class LoginRateLimitService {
    private final JdbcOperations jdbcTemplate;
    private final AuthSecurityProperties properties;
    private final Clock clock;

    public LoginRateLimitService(JdbcOperations jdbcTemplate, AuthSecurityProperties properties, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional
    public RateLimitResult acquire(String clientIp) {
        Instant now = clock.instant();
        Instant cutoff = now.minus(properties.getRateWindow());
        jdbcTemplate.update("DELETE FROM auth_login_rate_limits WHERE window_start < ?",
                Timestamp.from(now.minus(Duration.ofDays(1))));
        RateRow row = jdbcTemplate.queryForObject("""
                INSERT INTO auth_login_rate_limits (client_ip, window_start, request_count)
                VALUES (?, ?, 1)
                ON CONFLICT (client_ip) DO UPDATE SET
                    window_start = CASE
                        WHEN auth_login_rate_limits.window_start <= ? THEN EXCLUDED.window_start
                        ELSE auth_login_rate_limits.window_start END,
                    request_count = CASE
                        WHEN auth_login_rate_limits.window_start <= ? THEN 1
                        ELSE auth_login_rate_limits.request_count + 1 END
                RETURNING window_start, request_count
                """, (rs, rowNum) -> new RateRow(
                        rs.getTimestamp("window_start").toInstant(), rs.getInt("request_count")),
                clientIp, Timestamp.from(now), Timestamp.from(cutoff), Timestamp.from(cutoff));

        if (row.requestCount() <= properties.getRateLimit()) {
            return new RateLimitResult(true, 0);
        }
        Instant resetAt = row.windowStart().plus(properties.getRateWindow());
        long retryAfter = Math.max(1, ceilSeconds(Duration.between(now, resetAt)));
        return new RateLimitResult(false, retryAfter);
    }

    private long ceilSeconds(Duration duration) {
        return (duration.toMillis() + 999) / 1000;
    }

    record RateRow(Instant windowStart, int requestCount) {}
    public record RateLimitResult(boolean allowed, long retryAfterSeconds) {}
}
