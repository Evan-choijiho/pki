package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationExpiredException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.UNAUTHORIZED;
    static final ErrorType ERROR_TYPE = ErrorType.ClientSystem;
    static final ErrorReason ERROR_REASON = ErrorReason.Expired;
    static final int ERROR_CODE = 1203;

    public AuthenticationExpiredException(ErrorTarget errorTarget, Throwable cause) {
        super(RESPONSE_CODE, ERROR_TYPE, errorTarget, ERROR_REASON, ERROR_CODE, String.format("Authentication is expired. '%s'", errorTarget.toString()),
                cause);
    }
}
