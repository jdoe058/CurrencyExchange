package edu.zhekadoe.currencyexchange.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class BigDecimalConverter {

    public BigDecimal convert(String from) {
        return new BigDecimal(from);
    }

    boolean isValid(String from) {
        try {
            return Optional.ofNullable(from)
                    .map(BigDecimal::new)
                    .isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}
