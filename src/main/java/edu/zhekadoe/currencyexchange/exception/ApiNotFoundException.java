package edu.zhekadoe.currencyexchange.exception;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class ApiNotFoundException extends ApiException {
    public ApiNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return SC_NOT_FOUND;
    }
}
