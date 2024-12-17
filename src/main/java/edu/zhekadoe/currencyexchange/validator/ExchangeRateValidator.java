package edu.zhekadoe.currencyexchange.validator;

import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ExchangeRateValidator implements Validator<ExchangeDto> {
    private static final ExchangeRateValidator INSTANCE = new ExchangeRateValidator();

    public static ExchangeRateValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidationResult validate(ExchangeDto dto) {
        ValidationResult result = new ValidationResult();

        if (isCurrencyCodeInvalid(dto.getBaseCurrencyCode())) {
            result.add(ValidationError.of("invalid.baseCurrencyCode", "Invalid base currency code"));
        }

        if (isCurrencyCodeInvalid(dto.getTargetCurrencyCode())) {
            result.add(ValidationError.of("invalid.targetCurrencyCode", "Invalid target currency code"));
        }

        if (isBigDecimalInvalid(dto.getValue())) {
            result.add(ValidationError.of("invalid.rate", "Invalid rate"));
        }

        return result;
    }

    boolean isStringParamInvalid(String param) {
        return param == null || param.isBlank();
    }

    boolean isCurrencyCodeInvalid(String param) {
        return isStringParamInvalid(param) || !param.matches("[A-Za-z]{3}");
    }

    boolean isBigDecimalInvalid(String param) {
        if (isStringParamInvalid(param)) {
            return true;
        }

        try {
            BigDecimal bd = new BigDecimal(param);
            return bd.compareTo(BigDecimal.ZERO) <= 0; // Ensure the rate is positive
        } catch (NumberFormatException e) {
            return true; // If conversion fails, it's invalid
        }
    }
}
