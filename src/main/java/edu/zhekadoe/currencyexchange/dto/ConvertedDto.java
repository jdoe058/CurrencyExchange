package edu.zhekadoe.currencyexchange.dto;

import edu.zhekadoe.currencyexchange.entity.Currency;
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
