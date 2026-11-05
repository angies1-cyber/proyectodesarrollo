package gt.edu.umg.auth.service;

public class TurnstileUnavailableException extends RuntimeException {
    public TurnstileUnavailableException(Throwable cause) {
        super(cause);
    }
}
