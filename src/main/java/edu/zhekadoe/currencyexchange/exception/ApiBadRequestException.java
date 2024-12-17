package edu.zhekadoe.currencyexchange.exception;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class ApiBadRequestException extends ApiException {
    public ApiBadRequestException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return SC_BAD_REQUEST;
    }
}
