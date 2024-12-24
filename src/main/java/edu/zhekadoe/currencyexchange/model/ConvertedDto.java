package edu.zhekadoe.currencyexchange.model;

import lombok.Value;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class ConvertedDto {
    Currency baseCurrency;
    Currency targetCurrency;
    BigDecimal rate;
    BigDecimal amount;

    public BigDecimal getConvertedAmount() {
        return amount.multiply(rate);
    }
}
