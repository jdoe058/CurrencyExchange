package edu.zhekadoe.currencyexchange.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class CurrencyDto {
    String name;
    String code;
    String sign;
}
