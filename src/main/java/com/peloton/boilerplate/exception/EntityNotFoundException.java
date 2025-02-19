package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.NOT_FOUND;
    static final ErrorReason ERROR_REASON = ErrorReason.EntityNotFound;
    static final int ERROR_CODE = 1002;

    public EntityNotFoundException(ErrorType errorType, String errorTarget, String clientMessage, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, ERROR_REASON, ERROR_CODE, clientMessage, cause);
    }

    public EntityNotFoundException(ErrorType errorType, ErrorTarget errorTarget, String clientMessage, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, ERROR_REASON, ERROR_CODE, clientMessage, cause);
    }
}
