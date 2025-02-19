package com.peloton.boilerplate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

public class ClientRequestNotFoundException extends ServiceException{
    static final HttpStatus RESPONSE_CODE = HttpStatus.NOT_FOUND;
    static final ServiceException.ErrorReason ERROR_REASON = ErrorReason.RequestPathNotFound;
    static final int ERROR_CODE = 1101;

    public ClientRequestNotFoundException(ErrorType errorType, String errorTarget, Throwable cause) {
        super(RESPONSE_CODE, errorType, errorTarget, ERROR_REASON, ERROR_CODE, String.format("Request URL '%s' is not found", errorTarget), cause);
    }

    public ClientRequestNotFoundException(NoHandlerFoundException e) {
        this(ErrorType.ClientSystem, e.getRequestURL(), e.getCause());
    }
}
