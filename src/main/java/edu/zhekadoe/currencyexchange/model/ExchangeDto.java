package edu.zhekadoe.currencyexchange.model;

import java.math.BigDecimal;

public record ExchangeDto(CurrencyPairCodesDto codes, BigDecimal value) {

    public static ExchangeDto of(CurrencyPairCodesDto dto, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        try {
            BigDecimal decimal = new BigDecimal(value);
            if (decimal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Value must be greater than zero");
            }

            return new ExchangeDto(dto, decimal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid currency code: " + value);
        }
    }

    public static ExchangeDto of(String path, String value) {
        return ExchangeDto.of(CurrencyPairCodesDto.of(path), value);
    }

    public static ExchangeDto of(String baseCurrencyCode, String targetCurrencyCode, String value) {
        return ExchangeDto.of(CurrencyPairCodesDto.of(baseCurrencyCode, targetCurrencyCode), value);
    }
}
