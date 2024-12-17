package edu.zhekadoe.currencyexchange.validator;

import lombok.Value;

@Value(staticConstructor = "of")
public class ValidationError {
    String code;
    String message;
}
