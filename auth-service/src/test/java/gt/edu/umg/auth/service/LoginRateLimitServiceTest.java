package gt.edu.umg.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginRateLimitServiceTest {
    @Mock
    private JdbcOperations jdbcTemplate;
    private LoginRateLimitService service;

    @BeforeEach
    void setUp() {
        service = new LoginRateLimitService(jdbcTemplate, new AuthSecurityProperties(),
                Clock.fixed(Instant.parse("2026-11-05T12:00:30Z"), ZoneOffset.UTC));
    }

    @Test
    @SuppressWarnings("unchecked")
    void decimaSolicitudSePermite() {
        mockReturnedRow(10, Instant.parse("2026-11-05T12:00:00Z"));

        var result = service.acquire("203.0.113.10");

        assertTrue(result.allowed());
        assertEquals(0, result.retryAfterSeconds());
    }

    @Test
    @SuppressWarnings("unchecked")
    void undecimaSolicitudSeRechazaConRetryAfter() {
        mockReturnedRow(11, Instant.parse("2026-11-05T12:00:00Z"));

        var result = service.acquire("203.0.113.10");

        assertFalse(result.allowed());
        assertEquals(30, result.retryAfterSeconds());
    }

    @SuppressWarnings("unchecked")
    private void mockReturnedRow(int count, Instant windowStart) {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any(Object[].class)))
                .thenReturn(new LoginRateLimitService.RateRow(windowStart, count));
    }
}
