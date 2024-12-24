package edu.zhekadoe.currencyexchange.model;

public record ExchangeDto(CurrencyPairCodesDto codes, String value) {

    public static ExchangeDto of(String baseCurrencyCode, String targetCurrencyCode, String value) {
        return new ExchangeDto(CurrencyPairCodesDto.of(baseCurrencyCode, targetCurrencyCode), value);
    }
}
