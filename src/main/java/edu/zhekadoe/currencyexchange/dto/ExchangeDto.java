package edu.zhekadoe.currencyexchange.dto;

import lombok.Value;

import java.math.BigDecimal;

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
