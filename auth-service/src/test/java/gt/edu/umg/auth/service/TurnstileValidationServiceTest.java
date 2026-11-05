package gt.edu.umg.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TurnstileValidationServiceTest {
    private MockRestServiceServer server;
    private TurnstileValidationService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://challenges.cloudflare.com/turnstile/v0");
        server = MockRestServiceServer.bindTo(builder).build();
        TurnstileProperties properties = new TurnstileProperties();
        properties.setSecretKey("test-secret");
        properties.setAllowedHostnames(List.of("login.example.com"));
        service = new TurnstileValidationService(builder.build(), properties);
    }

    @Test
    void acceptsMatchingActionAndHostnameAndSendsAllFields() {
        server.expect(requestTo("https://challenges.cloudflare.com/turnstile/v0/siteverify"))
                .andExpect(content().string(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("secret=test-secret"),
                        org.hamcrest.Matchers.containsString("response=test-token"),
                        org.hamcrest.Matchers.containsString("remoteip=203.0.113.5"))))
                .andRespond(withSuccess("""
                        {"success":true,"action":"login","hostname":"login.example.com"}
                        """, MediaType.APPLICATION_JSON));

        assertDoesNotThrow(() -> service.validate("test-token", "203.0.113.5"));
        server.verify();
    }

    @Test
    void rejectsMissingTokenWithoutCallingCloudflare() {
        assertThrows(InvalidTurnstileException.class, () -> service.validate(" ", "203.0.113.5"));
        server.verify();
    }

    @Test
    void rejectsWrongActionOrHostname() {
        server.expect(requestTo("https://challenges.cloudflare.com/turnstile/v0/siteverify"))
                .andRespond(withSuccess("""
                        {"success":true,"action":"register","hostname":"other.example.com"}
                        """, MediaType.APPLICATION_JSON));

        assertThrows(InvalidTurnstileException.class,
                () -> service.validate("test-token", "203.0.113.5"));
    }

    @Test
    void rejectsProductionValidationWithoutConfiguredHostname() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://challenges.cloudflare.com/turnstile/v0");
        server = MockRestServiceServer.bindTo(builder).build();
        TurnstileProperties properties = new TurnstileProperties();
        properties.setSecretKey("test-secret");
        properties.setRequireHostname(true);
        service = new TurnstileValidationService(builder.build(), properties);
        server.expect(requestTo("https://challenges.cloudflare.com/turnstile/v0/siteverify"))
                .andRespond(withSuccess("""
                        {"success":true,"action":"login","hostname":"login.example.com"}
                        """, MediaType.APPLICATION_JSON));

        assertThrows(InvalidTurnstileException.class,
                () -> service.validate("test-token", "203.0.113.5"));
    }

    @Test
    void mapsCloudflareFailureToUnavailable() {
        server.expect(requestTo("https://challenges.cloudflare.com/turnstile/v0/siteverify"))
                .andRespond(withServerError());

        assertThrows(TurnstileUnavailableException.class,
                () -> service.validate("test-token", "203.0.113.5"));
    }
}
