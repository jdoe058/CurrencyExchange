package edu.zhekadoe.currencyexchange.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ExchangeDto {
    String baseCurrencyCode;
    String targetCurrencyCode;
    String value;

    public String getBaseCurrencyCode() {
        return baseCurrencyCode.toUpperCase();
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode.toUpperCase();
    }
}
