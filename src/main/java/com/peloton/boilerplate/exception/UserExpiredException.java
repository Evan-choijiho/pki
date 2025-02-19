package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class UserExpiredException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.UNAUTHORIZED;
    static final ErrorType ERROR_TYPE = ErrorType.ClientSystem;
    static final ErrorReason ERROR_REASON = ErrorReason.UnAuthorized;
    static final int ERROR_CODE = 1202;

    public UserExpiredException(ErrorTarget errorTarget, Throwable cause) {
        super(RESPONSE_CODE, ERROR_TYPE, errorTarget, ERROR_REASON, ERROR_CODE, String.format("Authentication is failed. expiration '%s'", errorTarget.toString()), cause);
    }
}
