package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class OAuthFailedException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.UNAUTHORIZED;
    static final ErrorType ERROR_TYPE = ErrorType.ClientSystem;
    static final ErrorTarget ERROR_TARGET = ErrorTarget.OAuth;
    static final ErrorReason ERROR_REASON = ErrorReason.UnAuthorized;
    static final int ERROR_CODE = 1206;

    public OAuthFailedException(Throwable cause) {
        super(RESPONSE_CODE, ERROR_TYPE, ERROR_TARGET, ERROR_REASON, ERROR_CODE, String.format("Authentication is failed. '%s'", ERROR_TYPE.toString()), cause);
    }
}
