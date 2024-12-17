package edu.zhekadoe.currencyexchange.validator;

public interface Validator<T> {
    ValidationResult validate(T object);
}
