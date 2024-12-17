package edu.zhekadoe.currencyexchange.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class ExchangeDto {
    String baseCurrencyCode;
    String targetCurrencyCode;
    String value;
}
