package edu.zhekadoe.currencyexchange.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class FindExchangeRateDto {
    String baseCurrencyCode;
    String targetCurrencyCode;
}
