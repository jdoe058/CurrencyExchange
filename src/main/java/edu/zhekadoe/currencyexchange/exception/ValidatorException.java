package edu.zhekadoe.currencyexchange.exception;

import lombok.Getter;
import edu.zhekadoe.currencyexchange.validator.ValidationError;

import java.util.List;

@Getter
public class ValidatorException extends RuntimeException {

    private final List<ValidationError> validationErrors;

    public ValidatorException(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
