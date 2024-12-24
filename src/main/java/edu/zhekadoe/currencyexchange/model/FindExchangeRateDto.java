package edu.zhekadoe.currencyexchange.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class FindExchangeRateDto {
    String baseCurrencyCode;
    String targetCurrencyCode;
}
