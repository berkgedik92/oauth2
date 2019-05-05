package project.diagram.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthProcessException extends AuthenticationException {
    public AuthProcessException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthProcessException(String msg) {
        super(msg);
    }
}
