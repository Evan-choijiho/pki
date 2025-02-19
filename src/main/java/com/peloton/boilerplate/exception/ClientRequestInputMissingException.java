package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class ClientRequestInputMissingException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.BAD_REQUEST;
    static final ErrorReason ERROR_REASON = ErrorReason.Blank;
    static final int ERROR_CODE = 1102;

    public ClientRequestInputMissingException(ErrorType errorType, String errorTarget, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, ERROR_REASON, ERROR_CODE, String.format("Request parameter '%s' is missing", errorTarget), cause);
    }

    public ClientRequestInputMissingException(ErrorType errorType, ErrorTarget errorTarget, Throwable cause) {
        this(errorType, errorTarget.name(), cause);
    }

    public ClientRequestInputMissingException(Throwable cause) {
        super(RESPONSE_CODE, ErrorType.ClientSystem, ErrorTarget.RequestParameter, ERROR_REASON, ERROR_CODE,
                ((cause != null) ? cause.getMessage() : "Request parameter is missing"), cause);
    }
}
