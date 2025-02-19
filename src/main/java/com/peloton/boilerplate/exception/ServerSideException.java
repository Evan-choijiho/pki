package com.peloton.boilerplate.exception;

import com.peloton.boilerplate.util.WebLogUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServerSideException extends ServiceException {
    static public final HttpStatus RESPONSE_CODE = HttpStatus.INTERNAL_SERVER_ERROR;
    static public final ErrorType DEFAULT_ERROR_TYPE = ServiceException.ErrorType.ServerSystem;
    static public final ErrorTarget DEFAULT_ERROR_TARGET = ServiceException.ErrorTarget.ServerSystem;
    static public final ErrorReason DEFAULT_ERROR_REASON = ServiceException.ErrorReason.InternalServerError;

    static public final int DEFAULT_ERROR_CODE = 2001;
    static public final String DEFAULT_CLIENT_MESSAGE = "Internal Server Error";

    protected String serverMessage;

    public ServerSideException(String serverMessage, HttpStatus responseCode, int errorCode, Throwable cause) {
        super(responseCode, DEFAULT_ERROR_TYPE, DEFAULT_ERROR_TARGET, DEFAULT_ERROR_REASON, errorCode, DEFAULT_CLIENT_MESSAGE, cause);
        this.serverMessage = serverMessage;
    }

    public ServerSideException(String serverMessage, int errorCode, Throwable cause) {
        this(serverMessage, RESPONSE_CODE, errorCode, cause);
    }

    public ServerSideException(Throwable cause) {
        this(cause.getMessage(), RESPONSE_CODE, DEFAULT_ERROR_CODE, cause);
    }

    public String toLogMessage() {
        return String.format("'%s' %s 'error:%d' '%s'", WebLogUtils.getRequestId(), this.getClass()
                        .getSimpleName(),
                errorCode, serverMessage);
    }
}
