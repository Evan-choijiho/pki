package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class ClientRequestInputInvalidException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.BAD_REQUEST;
    static final ErrorReason ERROR_REASON = ErrorReason.Invalid;
    static final int ERROR_CODE = 1104;

    public ClientRequestInputInvalidException(ErrorType errorType, String errorTarget, ErrorReason errorReason, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, errorReason, ERROR_CODE, String.format("%s", errorTarget), cause);
    }

    public ClientRequestInputInvalidException(ErrorType errorType, ErrorTarget errorTarget, ErrorReason errorReason, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, errorReason, ERROR_CODE, String.format("Request parameter '%s' is invalid", errorTarget.name()), cause);
    }

    public ClientRequestInputInvalidException(ErrorType errorType, String errorTarget, Throwable cause) {
        this(errorType, errorTarget, ERROR_REASON, cause);
    }

    public ClientRequestInputInvalidException(ErrorType errorType, ErrorTarget errorTarget, Throwable cause) {
        this(errorType, errorTarget, ERROR_REASON, cause);
    }
}
