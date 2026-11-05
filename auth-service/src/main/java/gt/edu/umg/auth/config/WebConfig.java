package gt.edu.umg.auth.config;

import gt.edu.umg.auth.service.LoginRateLimitService;
import gt.edu.umg.auth.web.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginRateLimitService rateLimitService;

    public WebConfig(LoginRateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginRateLimitInterceptor(rateLimitService))
                .order(Ordered.HIGHEST_PRECEDENCE)
                .addPathPatterns("/api/v1/auth/login");
    }

    private record LoginRateLimitInterceptor(LoginRateLimitService rateLimitService)
            implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (!"POST".equals(request.getMethod())) {
                return true;
            }
            var result = rateLimitService.acquire(request.getRemoteAddr());
            if (!result.allowed()) {
                throw new RateLimitExceededException(result.retryAfterSeconds());
            }
            return true;
        }
    }
}
