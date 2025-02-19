package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;

public class ClientRequestDataInvalidException extends ServiceException {
    static final HttpStatus RESPONSE_CODE = HttpStatus.BAD_REQUEST;
    static final int ERROR_CODE = 1103;

    public ClientRequestDataInvalidException(ErrorType errorType, ErrorTarget errorTarget, ErrorReason errorReason, String errorMessage, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, errorReason, ERROR_CODE, errorMessage, cause);
    }

    public ClientRequestDataInvalidException(ErrorType errorType, ErrorTarget errorTarget, ErrorReason errorReason, Throwable cause) {
        this(errorType, errorTarget, errorReason, "Request is invalid", cause);
    }
}
