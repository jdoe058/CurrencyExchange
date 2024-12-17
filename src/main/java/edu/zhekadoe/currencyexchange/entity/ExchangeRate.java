package edu.zhekadoe.currencyexchange.entity;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

public @Data(staticConstructor = "of") class ExchangeRate {
    private @NonNull Long id;
    private @NonNull Currency baseCurrency;
    private @NonNull Currency targetCurrency;
    private @NonNull BigDecimal rate;
}
