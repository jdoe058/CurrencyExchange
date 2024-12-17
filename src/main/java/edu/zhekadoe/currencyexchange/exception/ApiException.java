package edu.zhekadoe.currencyexchange.exception;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class ApiException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_PATTERN = "{\"message\":\"%s\"}";

    public ApiException(String message) {
        super(message);
    }

    public int getStatus() {
        return SC_INTERNAL_SERVER_ERROR;
    }

    public String getBody() {
        return EXCEPTION_MESSAGE_PATTERN.formatted(getMessage());
    }

}
