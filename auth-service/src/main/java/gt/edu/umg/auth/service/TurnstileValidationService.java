package gt.edu.umg.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class TurnstileValidationService {
    private static final Logger log = LoggerFactory.getLogger(TurnstileValidationService.class);
    private final RestClient restClient;
    private final TurnstileProperties properties;

    public TurnstileValidationService(RestClient turnstileRestClient, TurnstileProperties properties) {
        this.restClient = turnstileRestClient;
        this.properties = properties;
    }

    public void validate(String token, String remoteIp) {
        if (token == null || token.isBlank()) {
            throw new InvalidTurnstileException();
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", properties.getSecretKey());
        form.add("response", token);
        form.add("remoteip", remoteIp);

        final SiteVerifyResponse result;
        try {
            result = restClient.post()
                    .uri("/siteverify")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(SiteVerifyResponse.class);
        } catch (RestClientException exception) {
            log.error("Error communicating with Cloudflare Turnstile API: {}", exception.getMessage(), exception);
            throw new TurnstileUnavailableException(exception);
        }

        if (result == null) {
            log.warn("Turnstile validation failed: response from Cloudflare was null");
            throw new InvalidTurnstileException();
        }

        if (!result.success()) {
            log.warn("Turnstile validation failed by Cloudflare. success=false, errorCodes={}, hostname={}, action={}",
                    result.errorCodes(), result.hostname(), result.action());
            throw new InvalidTurnstileException();
        }

        if (!actionAllowed(result.action())) {
            log.warn("Turnstile validation failed: action mismatch. Expected '{}', got '{}'",
                    properties.getExpectedAction(), result.action());
            throw new InvalidTurnstileException();
        }

        if (!hostnameAllowed(result.hostname())) {
            log.warn("Turnstile validation failed: hostname mismatch. Allowed={}, got '{}'",
                    properties.getAllowedHostnames(), result.hostname());
            throw new InvalidTurnstileException();
        }
    }

    private boolean actionAllowed(String action) {
        String expected = properties.getExpectedAction();
        return expected == null || expected.isBlank() || expected.equals(action);
    }

    private boolean hostnameAllowed(String hostname) {
        List<String> configured = properties.getAllowedHostnames().stream()
                .filter(value -> value != null && !value.isBlank())
                .toList();
        if (configured.isEmpty()) {
            return !properties.isRequireHostname();
        }
        return hostname != null && configured.stream().anyMatch(hostname::equalsIgnoreCase);
    }

    record SiteVerifyResponse(
            boolean success,
            String action,
            String hostname,
            @JsonProperty("error-codes") List<String> errorCodes
    ) {}
}
