package gt.edu.umg.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class TurnstileConfig {
    @Bean
    RestClient turnstileRestClient(RestClient.Builder builder) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return builder
                .baseUrl("https://challenges.cloudflare.com/turnstile/v0")
                .requestFactory(requestFactory)
                .build();
    }
}
