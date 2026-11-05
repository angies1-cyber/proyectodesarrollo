package gt.edu.umg.auth.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.auth-security")
public class AuthSecurityProperties {
    private int rateLimit = 10;
    private Duration rateWindow = Duration.ofMinutes(1);
    private int maxFailedAttempts = 5;
    private Duration lockDuration = Duration.ofMinutes(15);

    public int getRateLimit() { return rateLimit; }
    public void setRateLimit(int rateLimit) { this.rateLimit = rateLimit; }
    public Duration getRateWindow() { return rateWindow; }
    public void setRateWindow(Duration rateWindow) { this.rateWindow = rateWindow; }
    public int getMaxFailedAttempts() { return maxFailedAttempts; }
    public void setMaxFailedAttempts(int maxFailedAttempts) { this.maxFailedAttempts = maxFailedAttempts; }
    public Duration getLockDuration() { return lockDuration; }
    public void setLockDuration(Duration lockDuration) { this.lockDuration = lockDuration; }
}
