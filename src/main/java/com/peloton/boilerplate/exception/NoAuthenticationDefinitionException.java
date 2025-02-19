package com.peloton.boilerplate.exception;

public class NoAuthenticationDefinitionException extends ServerSideException {
    static final int ERROR_CODE = 5006;

    public NoAuthenticationDefinitionException(String path, String method, Throwable cause) {
        super(String.format("No permission exists for '%s:%s'", path, method, cause), ERROR_CODE, null);
    }
}
