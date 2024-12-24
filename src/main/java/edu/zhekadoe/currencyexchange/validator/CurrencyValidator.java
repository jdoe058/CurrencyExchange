package edu.zhekadoe.currencyexchange.validator;

import edu.zhekadoe.currencyexchange.model.CurrencyDto;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CurrencyValidator implements Validator<CurrencyDto> {
    private static final CurrencyValidator INSTANCE = new CurrencyValidator();

    public static CurrencyValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidationResult validate(CurrencyDto object) {
        ValidationResult validationResult = new ValidationResult();

        if (object.getCode() == null || !object.getCode().matches("^[A-Za-z]{3}$")) {
            validationResult.add(ValidationError.of("invalid.code", "Invalid currency code"));
        }
        if (object.getName() == null || object.getName().trim().isEmpty()) {
            validationResult.add(ValidationError.of("invalid.name", "Invalid currency name"));
        }
        if(object.getSign() == null || object.getSign().trim().isEmpty()) {
            validationResult.add(ValidationError.of("invalid.sign", "Invalid currency sign"));
        }

        return validationResult;
    }
}
