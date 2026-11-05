package gt.edu.umg.auth.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("app.turnstile")
public class TurnstileProperties {
    private String secretKey;
    private String expectedAction = "login";
    private List<String> allowedHostnames = new ArrayList<>();
    private boolean requireHostname;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getExpectedAction() {
        return expectedAction;
    }

    public void setExpectedAction(String expectedAction) {
        this.expectedAction = expectedAction;
    }

    public List<String> getAllowedHostnames() {
        return allowedHostnames;
    }

    public void setAllowedHostnames(List<String> allowedHostnames) {
        this.allowedHostnames = allowedHostnames;
    }

    public boolean isRequireHostname() {
        return requireHostname;
    }

    public void setRequireHostname(boolean requireHostname) {
        this.requireHostname = requireHostname;
    }
}
