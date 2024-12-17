package edu.zhekadoe.currencyexchange.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class ValidationResult {

    private final List<ValidationError> validationErrors = new ArrayList<>();

    public void add(ValidationError validationError) {
        validationErrors.add(validationError);
    }

    public boolean isValid() {
        return validationErrors.isEmpty();
    }
}
