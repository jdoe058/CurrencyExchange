package edu.zhekadoe.currencyexchange.exception;

import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;

public class ApiConflictException extends ApiException {
    public ApiConflictException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return SC_CONFLICT;
    }
}
