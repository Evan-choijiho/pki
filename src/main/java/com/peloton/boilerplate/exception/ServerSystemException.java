package com.peloton.boilerplate.exception;

public class ServerSystemException extends ServerSideException {
    static final int ERROR_CODE = 2002;
    static final String DEFAULT_SERVER_MESSAGE = "ServerSystemException";

    public ServerSystemException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

    public ServerSystemException(Throwable cause) {
        this(DEFAULT_SERVER_MESSAGE, cause);
    }
}
